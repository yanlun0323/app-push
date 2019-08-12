package com.xz.msg.push.service.redis.topic;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.service.push.thirdparty.ThirdPartyPushService;
import com.xz.msg.push.utils.RedisKeyConfigure;

/**
 * 
 * @author Yan
 *
 */
@Service("topicMessageWithAjiaeduListener")
public class TopicMessageWithAjiaeduListener extends AbstractTopicMessageListener {

	private static final Log logger = LogFactory.getLog(TopicMessageWithAjiaeduListener.class);

	@Resource(name = "JPushService")
	private ThirdPartyPushService thirdPartyPushService;
	
    @Override
    public String getQueueKey() {
        return RedisKeyConfigure.AJIA_EDU_TOPIC_KEY;
    }

    @Override
    protected void doProcess(byte[] channelBytes, String msgContent) {
    	try {
			JPushMessageBuilder builder = JSON.parseObject(msgContent, JPushMessageBuilder.class);
			thirdPartyPushService.smartInvoke(builder);
		} catch (Exception e) {
			logger.error(e, e.getCause());
			logger.error("消息推送失败,被弹回任务队列->" + msgContent);
			JPushAPIExceptionHandler(channelBytes, msgContent, e);
		}
    }
}
