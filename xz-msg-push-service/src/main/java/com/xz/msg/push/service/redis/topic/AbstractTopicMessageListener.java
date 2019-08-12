package com.xz.msg.push.service.redis.topic;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.xz.msg.push.entity.MessageBuilder;
import com.xz.msg.push.service.push.PushService;
import com.xz.msg.push.service.push.thirdparty.jpush.JPushResponseCode;
import com.xz.msg.push.service.redis.RedisService;
import com.xz.msg.push.service.redis.lock.RedisDistributedLock;
import com.xz.msg.push.utils.RedisKeyConfigure;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;

/**
 * 
 * @author Yan
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractTopicMessageListener implements MessageListener {

	private static final Log logger = LogFactory.getLog(AbstractTopicMessageListener.class);

	protected static final Integer DEFAULT_PSUH_DELAYED = 1000;// 默认延时1s重入消息队列

	private static final Integer BRPOP_ITEM_MESSAGE_INDEX = 1;

	private RedisDistributedLock lock;

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private PushService pushService;

	@Resource(name = "redisCache")
	private RedisService redisService;

	public abstract String getQueueKey();

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String msgContent = (String) this.redisTemplate.getValueSerializer().deserialize(message.getBody());

		if (StringUtils.hasText(msgContent)) {
			lock = new RedisDistributedLock(redisTemplate, calcRedisDistributedLockKey(msgContent));
			if (lock.tryLock(1000)) {
				try {
					List<String> item = redisService.bRPopItem(getQueueKey(), String.class, 30);
					if (!item.isEmpty()) {
						doProcess(message.getChannel(), item.get(BRPOP_ITEM_MESSAGE_INDEX));
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e.getCause());
				} finally {
					lock.unlock();
				}
			}
		}
	}

	/**
	 * 分布式锁Key计算
	 * 
	 * @param msgContent
	 * @return
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	private String calcRedisDistributedLockKey(String msgContent) {
		MessageBuilder builder = JSON.parseObject(msgContent, MessageBuilder.class);

		StringBuilder lockKeyBuilder = new StringBuilder(getQueueKey());
		lockKeyBuilder.append(builder.getMessageId()).append(":").append(System.currentTimeMillis());

		return lockKeyBuilder.toString();
	}

	/**
	 * 消息发送失败的重试机制，延迟N秒入消息队列
	 * 
	 * @param channel
	 * @param msg
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	protected void resendMessage(byte[] channelBytes, String msg, Integer pushDelayed) {
		if (StringUtils.hasText(msg)) {
			try {
				Thread.sleep(pushDelayed);
			} catch (InterruptedException e) {// 延时失败，继续进行下面的步骤
				logger.error(e.getMessage(), e.getCause());
			}

			String channel = (String) this.redisTemplate.getStringSerializer().deserialize(channelBytes);

			logger.debug("重回消息队列->[" + channel + "," + msg + "]");

			pushService.publish(msg, channel, Arrays.asList(getQueueKey()));
		}
	}

	/**
	 * 消息发送失败，需要持久化的消息存入队列
	 * 
	 * @param channel
	 * @param msg
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	protected void persistenceFailureMessage(byte[] channelBytes, String msg) {
		if (StringUtils.hasText(msg)) {

			StringBuilder channel = new StringBuilder(RedisKeyConfigure.ERROR_QUEUE_PREFIX);
			channel.append((String) this.redisTemplate.getStringSerializer().deserialize(channelBytes));

			logger.debug("消息推送失败,存入错误消息队列->[" + channel.toString() + "," + msg + "]");

			StringBuilder redisKey = new StringBuilder(channel);
			redisKey.append("_").append(getQueueKey());

			this.redisService.pushListItem(redisKey.toString(), msg);
		}
	}

	/**
	 * 针对JPush API调用异常的处理
	 * @param channelBytes
	 * @param msgContent
	 * @param e
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	protected synchronized void JPushAPIExceptionHandler(byte[] channelBytes, String msgContent, Exception e) {
		// JPUSH错误消息判定，如果非网络请求导致推送失败则记录错误信息，存入失败消息队列且不继续重试任务
		if (e instanceof APIConnectionException) {
			resendMessage(channelBytes, msgContent, DEFAULT_PSUH_DELAYED);
		} else if (e instanceof APIRequestException) {
			Integer errorCode = ((APIRequestException) e).getErrorCode();
			if (JPushResponseCode.NEED_RETRY_RESPONSE_CODE.contains(errorCode)) {
				//2002 API调用频率超出该应用的限制
				invokeRateLimitExceptionHandler(msgContent, errorCode);
				
				resendMessage(channelBytes, msgContent, DEFAULT_PSUH_DELAYED);
			} else {
				// 推送失败，消息存入错误消息队列
				persistenceFailureMessage(channelBytes, msgContent);
			}
		} else {
			// 推送失败，消息存入错误消息队列
			persistenceFailureMessage(channelBytes, msgContent);
		}
	}

	/**
	 * 分布式环境-针对API调用频率超出该应用的限制的处理
	 * @param msgContent
	 * @param errorCode
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private void invokeRateLimitExceptionHandler(String msgContent, Integer errorCode) {
		if (JPushResponseCode.INVOKE_RATE_LIMIT.equals(errorCode)) {
			MessageBuilder builder = JSON.parseObject(msgContent, MessageBuilder.class);
			
			Integer rateLimitRest = redisService
					.read(RedisKeyConfigure.RateLimitRestCacheKey(builder.getAppKey()), Integer.class);
			
			if (rateLimitRest != null && rateLimitRest > 0) {
				try {
					logger.debug("达到推送频率瓶颈,线程等待[" + (rateLimitRest * 1000 + 100) + "ms],消息体[" + msgContent + "]");
					Thread.sleep(rateLimitRest * 1000 + 100);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage(), e1.getCause());
				}
			}
		}
	}
	
	protected abstract void doProcess(byte[] channelBytes, String msgContent);
}
