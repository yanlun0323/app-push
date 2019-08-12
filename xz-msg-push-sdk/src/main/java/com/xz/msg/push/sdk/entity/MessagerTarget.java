package com.xz.msg.push.sdk.entity;

import java.util.Set;

public class MessagerTarget {
	
	public static final String ALIAS = "alias";
	public static final String REGISTRATIONIDS = "registrationIds";
	public static final String TAGS = "tags";

	private Set<String> registrationIds;

	private Set<String> alias;

	private Set<String> tags;
	private Set<String> tagsAnd;// 数组。多个标签之间是 AND 关系，即取交集。
	private Set<String> tagsNot;// 数组。多个标签之间，先取多标签的并集，再对该结果取补集。

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
}
