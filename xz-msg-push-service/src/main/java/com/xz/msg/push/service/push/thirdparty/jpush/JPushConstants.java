package com.xz.msg.push.service.push.thirdparty.jpush;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月23日 下午4:37:52
 */
public abstract class JPushConstants {

	//阙值keys常量定义
	public static final String KEYWORD_TAG_MAX_KEY = "keywordTagMax";
	public static final String KEYWORD_TAG_AND_MAX_KEY = "keywordTagAndMax";
	public static final String KEYWORD_TAG_NOT_MAX_KEY = "keywordTagNotMax";
	public static final String KEYWORD_ALIAS_MAX_KEY = "keywordAliasMax";
	public static final String KEYWORD_REGISTRATIONID_MAX_KEY = "keywordRegistrationIdMax";
	
	
	//消息类型定义:NOTIFICATION->通知，MESSAGE->透传消息（应用内消息）
	public static final String NOTIFICATION = "NOTIFICATION";
	public static final String MESSAGE = "MESSAGE";
	
	public static final String EXTRAS_KEY = "extra";
	
	//其他核心参数定义
	public static final Integer REQUEST_TOO_FAST_WAITING = 1000;// 請求頻率過快等待時間(ms)
	public static final Long 	  REQUEST_TOO_FAST_REDIS_EXPIRED = Long.valueOf(REQUEST_TOO_FAST_WAITING / 1000);// 請求頻率過快状态失效时间(s)
	public static final Integer API_EXCEPTION_RETRY_WAITING = 5 * 1000;// API調用異常，重試等待時間(ms)
	public static final Integer MAX_RETRY_TIMES = 5;//失败最大尝试次数
	
	public static final float DEFAULT_NEED_WAITING_USAGE_RATE = 0.8f;// 短时间超过80%，执行线程等待
}
