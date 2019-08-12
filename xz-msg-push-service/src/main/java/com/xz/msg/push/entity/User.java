package com.xz.msg.push.entity;

import java.util.List;

public class User {

	private int id;
	private String name;
	private String appkey;
	private String appsecret;
	private List<String> topic;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public List<String> getTopic() {
		return topic;
	}

	public void setTopic(List<String> topic) {
		this.topic = topic;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", appkey=" + appkey + ", appsecret=" + appsecret + ", topic="
				+ topic + "]";
	}
}
