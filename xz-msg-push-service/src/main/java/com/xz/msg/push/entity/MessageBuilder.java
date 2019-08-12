package com.xz.msg.push.entity;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.util.StringUtils;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月18日 上午10:05:42
 */
public class MessageBuilder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8040180842273705934L;

	private String appKey;//第三方推送appKey
	
	private String masterSecret;//第三方推送masterSecret
	
	private String messageId;//消息唯一标识
	
	private String title;//消息标题
	
	private String message;//发送的消息体
	
	private String type;//消息类型:NOTIFICATION->通知，MESSAGE->透传消息（应用内消息）
	
	private String  transparentTransmissionContent;//此属性不为空则通知的同时需要透传消息
	
	public MessageBuilder() {
		
	}
	
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getMasterSecret() {
		return masterSecret;
	}

	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}

	public String getMessageId() {
		return StringUtils.hasText(messageId) ? messageId : UUID.randomUUID().toString().replaceAll("-", "");
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getTransparentTransmissionContent() {
		return transparentTransmissionContent;
	}

	public void setTransparentTransmissionContent(String transparentTransmissionContent) {
		this.transparentTransmissionContent = transparentTransmissionContent;
	}

}
