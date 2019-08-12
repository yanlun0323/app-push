package com.xz.msg.push.service.push.thirdparty.jpush.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.entity.MessageBuilder;
import com.xz.msg.push.service.push.thirdparty.ThirdPartyPushService;
import com.xz.msg.push.service.push.thirdparty.jpush.JPushBuilder;
import com.xz.msg.push.service.push.thirdparty.jpush.JPushCommonConfig;
import com.xz.msg.push.service.push.thirdparty.jpush.JPushConstants;
import com.xz.msg.push.service.push.thirdparty.jpush.JPushResponseCode;
import com.xz.msg.push.service.redis.RedisService;
import com.xz.msg.push.utils.CollectionUtils;
import com.xz.msg.push.utils.RedisKeyConfigure;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

/**
 * 极光推送API调用
 * 
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月19日 下午2:54:21
 */
@Service("JPushService")
public class JPushServiceImpl implements ThirdPartyPushService {

	private Log logger = LogFactory.getLog(JPushServiceImpl.class);

	@Value("${jpush.push.tag.max}")
	private Integer keywordTagMax;
	//目前不支持tagand和tagnot
	@Value("${jpush.push.tagand.max}")
	private Integer keywordTagAndMax;
	@Value("${jpush.push.tagnot.max}")
	private Integer keywordTagNotMax;
	@Value("${jpush.push.alias.max}")
	private Integer keywordAliasMax;
	@Value("${jpush.push.registrationid.max}")
	private Integer keywordRegistrationIdMax;
	
	@Value("${jpush.apns.production}")
	private Boolean APNSProduction;
	
	private Map<String, Integer> keywordsMax;
	
	@Resource(name = "redisCache")
	private RedisService redisService;
	
	@PostConstruct
	public void init(){
		keywordsMax = new HashMap<>();
		
		keywordsMax.put(JPushConstants.KEYWORD_TAG_MAX_KEY, keywordTagMax);
		keywordsMax.put(JPushConstants.KEYWORD_ALIAS_MAX_KEY, keywordAliasMax);
		keywordsMax.put(JPushConstants.KEYWORD_REGISTRATIONID_MAX_KEY, keywordRegistrationIdMax);
		keywordsMax.put(JPushConstants.KEYWORD_TAG_AND_MAX_KEY, keywordTagAndMax);
		keywordsMax.put(JPushConstants.KEYWORD_TAG_NOT_MAX_KEY, keywordTagNotMax);
	}
	
	/**
	 * 推送频率 免费版本的每个Appkey的最高推送频率为600次/分钟</br>
	 * 推送数量 通过控制台或API推送通知或消息，均不会限制推送的数量</br>
	 * 消息达到推送瓶颈，则需要等待【rateLimitReset】</br>
	 * 加入同步关键字synchronized后每秒能处理3个请求；去掉synchronized关键字后可高并发，当出现频率限制异常后会在重试的基础上进行等待；
	 */
	@Override
	public void smartInvoke(MessageBuilder message) throws APIConnectionException, APIRequestException {
		JPushMessageBuilder jmessage = (JPushMessageBuilder) message;
		logger.debug("Call ThirdParty Message Push API->MessageID:" + jmessage.getMessageId());

		// Must set
		jmessage.setApnsProduction(APNSProduction);
		List<PushPayload> pushPayloads = JPushBuilder.buildPushPayload(jmessage, keywordsMax);

		// 这里如果用stream api，则必须在内部捕捉sendPush方法产生的异常（会影响之后的流程）
		for (PushPayload pushPayload : pushPayloads) {
			smartAwait(jmessage);
			PushResult result = JPushCommonConfig.getJPushClientInstance(jmessage).sendPush(pushPayload);
			pushResultProcessing(message, jmessage, result);
		}
	}

	private synchronized void smartAwait(JPushMessageBuilder jmessage) {
		try {
			Integer rateLimitRest = redisService
					.read(RedisKeyConfigure.RateLimitRestCacheKey(jmessage.getAppKey()), Integer.class);
			if (rateLimitRest != null && rateLimitRest > 0) {
				logger.debug("达到推送频率瓶颈,线程等待[" + (rateLimitRest * 1000 + 100) + "ms]");
				Thread.sleep(rateLimitRest * 1000 + 100);
			} else {
				// 判定是否请求过快
				Boolean isFast = redisService.getRedisTemplate()
						.hasKey(RedisKeyConfigure.RequestTooFastCacheKey(jmessage.getAppKey()));
				if (isFast) {
					logger.debug("执行推送请求频率过快，等待[" + JPushConstants.REQUEST_TOO_FAST_WAITING + "ms]");
					Thread.sleep(JPushConstants.REQUEST_TOO_FAST_WAITING);
				}
			}
		} catch (InterruptedException e1) {
			logger.error(e1.getMessage(), e1.getCause());
		}
	}

	/**
	 * 推送结果处理</br>
	 * 判断是否达到推送频率上线，如果是则需要等待RateLimitReset对应的时间；</br>
	 * 如果推送数量短时间内达到80%，则线程等待N秒(高并发的情况下将失效)
	 * @param message
	 * @param jmessage
	 * @param result
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private void pushResultProcessing(MessageBuilder message, JPushMessageBuilder jmessage, PushResult result){
		logger.debug("JPush Result->" + JSON.toJSON(result));
		
		// 判断是否达到推送频率上线，如果是则需要等待RateLimitReset对应的时间；
		// 如果推送数量短时间（RateLimitReset>40）内达到80%，则线程等待N秒
		if (result != null && result.isResultOK()) {
			if (result.getRateLimitRemaining() == 1) {// 这里先调用API才能获取频率参数，所以用limitRemaining==1作为比较条件
				logger.debug("达到极光推送频率瓶颈[Quota:" + result.getRateLimitQuota() + "]，线程等待[" + result.getRateLimitReset() + "s],消息体[" + message.getMessage() + "]");
				
				long expired = Long.parseLong(String.valueOf(result.getRateLimitReset()));
				// 保存调用频率限制的重置时间并设置过期时间，作为重试等待的依据
				redisService.save(RedisKeyConfigure.RateLimitRestCacheKey(jmessage.getAppKey()), result.getRateLimitReset(), expired);
			} else if (result.getRateLimitReset() / 40 == 1
					&& result.getRateLimitRemaining() < result.getRateLimitQuota() * JPushConstants.DEFAULT_NEED_WAITING_USAGE_RATE) {
				redisService.save(RedisKeyConfigure.RequestTooFastCacheKey(jmessage.getAppKey()), "X", JPushConstants.REQUEST_TOO_FAST_REDIS_EXPIRED);
			}
			logger.info("消息推送成功->" + jmessage.getMessage());
		}
	}

	@Override
	public void setAliasAndTags(MessageBuilder message){
		try {
			JPushSetAliasAndTags(message);
		} catch (APIConnectionException | APIRequestException e) {
			logger.error(e);
			JPushDeviceAPIExceptionHandler(message, e, JPushConstants.MAX_RETRY_TIMES);
		}
	}
	
	/**
	 * 极光推送-绑定Alias或Tags
	 * @param message
	 * @throws APIConnectionException
	 * @throws APIRequestException
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private void JPushSetAliasAndTags(MessageBuilder message) throws APIConnectionException, APIRequestException {
		JPushMessageBuilder jmessage = (JPushMessageBuilder) message;
		JPushClient jPushClient = JPushCommonConfig.getJPushClientInstance(jmessage);
		
		jPushClient.updateDeviceTagAlias(CollectionUtils.findFirst(jmessage.getRegistrationIds()),
				CollectionUtils.findFirst(jmessage.getAlias()), jmessage.getTags(), null);
		
		logger.info("设备绑定成功->" + "tags:" + jmessage.getTags() + "\t alias:" + jmessage.getAlias());
	}
	
	/**
	 * Device-API自动重试
	 * @param message
	 * @param e
	 * @param retryTimes
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private void JPushDeviceAPIExceptionHandler(MessageBuilder message, Exception e, Integer retryTimes) {
		if (retryTimes > 0) {
			boolean APIConnectonExceptionRetry = (e instanceof APIConnectionException);
			boolean APIRequestExceptionRetry = (e instanceof APIRequestException
					&& JPushResponseCode.NEED_RETRY_RESPONSE_CODE.contains(((APIRequestException) e).getErrorCode()));
			if (APIConnectonExceptionRetry || APIRequestExceptionRetry) {
				// 等待5S后执行重试任务
				try {
					logger.debug("重试任务等待[" + JPushConstants.API_EXCEPTION_RETRY_WAITING + "ms]");
					Thread.sleep(JPushConstants.API_EXCEPTION_RETRY_WAITING);
				} catch (InterruptedException e2) {// 吞掉异常继续执行任务
					logger.error(e2.getMessage(), e2.getCause());
				}
				
				logger.debug("继续重试任务->消息体["+ JSON.toJSONString(message) +"]");
				
				try {
					this.JPushSetAliasAndTags(message);
				} catch (APIConnectionException | APIRequestException e1) {
					logger.error(e1.getMessage(), e1.getCause());
					JPushDeviceAPIExceptionHandler(message, e1, retryTimes--);
				}
			} else {
				logger.debug("终止尝试任务->不满足重试规则" + e.getMessage());
			}
		} else {
			logger.debug("终止尝试任务->" + e.getMessage());
		}
	}
}