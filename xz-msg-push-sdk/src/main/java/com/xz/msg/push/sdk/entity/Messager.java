package com.xz.msg.push.sdk.entity;

import java.io.Serializable;
import java.util.Set;

public class Messager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 766477028636491968L;

	private String appKey;// 应用appKey
	private String appSecret;// 应用appSecret

	private String title;// 消息标题

	private String message;// 发送的消息体
	
	private String type;//消息类型:NOTIFICATION->通知，MESSAGE->透传消息（应用内消息）
	
	private String  transparentTransmissionContent;//此属性不为空则通知的同时需要透传消息

	// targets
	private Set<String> registrationIds;
	private Set<String> alias;

	private Set<String> tags;
	private Set<String> tagsAnd;// 数组。多个标签之间是 AND 关系，即取交集。
	private Set<String> tagsNot;// 数组。多个标签之间，先取多标签的并集，再对该结果取补集。

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
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

	public Set<String> getRegistrationIds() {
		return registrationIds;
	}

	public void setRegistrationIds(Set<String> registrationIds) {
		this.registrationIds = registrationIds;
	}

	public Set<String> getAlias() {
		return alias;
	}

	public void setAlias(Set<String> alias) {
		this.alias = alias;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public Set<String> getTagsAnd() {
		return tagsAnd;
	}

	public void setTagsAnd(Set<String> tagsAnd) {
		this.tagsAnd = tagsAnd;
	}

	public Set<String> getTagsNot() {
		return tagsNot;
	}

	public void setTagsNot(Set<String> tagsNot) {
		this.tagsNot = tagsNot;
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