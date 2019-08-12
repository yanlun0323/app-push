package com.xz.msg.push.entity;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月22日 下午10:34:47
 */
public class JPushMessageBuilder extends MessageBuilder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2282806816710898006L;

	private Set<String> registrationIds;

	private Set<String> alias;

	private Set<String> tags;
	private Set<String> tagsAnd;//数组。多个标签之间是 AND 关系，即取交集。
	private Set<String> tagsNot;//数组。多个标签之间，先取多标签的并集，再对该结果取补集。
	
	private Boolean apnsProduction;//setApnsProduction 消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）

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

	public Boolean getApnsProduction() {
		return apnsProduction;
	}

	/**
	 * setApnsProduction 消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
	 * 
	 * @param apnsProduction
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public void setApnsProduction(Boolean apnsProduction) {
		this.apnsProduction = apnsProduction;
	}
}
