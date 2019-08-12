package com.xz.msg.push.service.push.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.xz.msg.push.entity.MessageBuilder;
import com.xz.msg.push.service.push.PushService;
import com.xz.msg.push.service.redis.RedisService;

/**
 * 
 * @author Yan
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class PushServiceImpl implements PushService {

	private static final Log logger = LogFactory.getLog(PushServiceImpl.class);

	@Resource
	private RedisTemplate redisTemplate;

	@Resource(name = "redisCache")
	private RedisService redisService;

	public void publish(String message, String channel, List<String> queueKeys) {
		if (message == null)
			return;
		if (!StringUtils.hasText(channel)) {
			logger.warn("<<<<<< Redis Publish Message Fail, Channel Is Null");
			return;
		}

		try {
			doPublish(message, channel, queueKeys);
		} catch (Throwable th) {
			logger.warn("<<<<<< Redis publish message fail", th);
			logger.warn("<<<<<< " + message);
		}
	}

	@Override
	public void publish(MessageBuilder message, String channel, List<String> queueKeys) {
		if (message == null)
			return;
		if (!StringUtils.hasText(channel)) {
			logger.warn("<<<<<< Redis Publish Message Fail, Channel Is Null");
			return;
		}

		String jsonString = JSONObject.toJSONString(message);
		try {
			doPublish(jsonString, channel, queueKeys);
		} catch (Throwable th) {
			logger.warn("<<<<<< Redis publish message fail", th);
			logger.warn("<<<<<< " + jsonString);
		}
	}
	
	private void doPublish(String message, String channel, List<String> queueKeys) {
		// 补偿机制
		if (queueKeys != null) {
			queueKeys.forEach(q -> this.redisService.pushListItem(q, message));
		}
		// 发布消息
		this.redisTemplate.convertAndSend(channel, message);
	}
}
