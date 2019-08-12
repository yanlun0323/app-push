package com.xz.msg.push.service.push;

import java.util.List;

import com.xz.msg.push.entity.MessageBuilder;

/**
 * 
 * @author Yan
 *
 */
public interface PushService {
	
	public void publish(String message, String channel, List<String> queueKeys);

    /**
     * 消息发布
     * @param message
     */

    void publish(MessageBuilder message, String channel, List<String> queueKeys);
}
