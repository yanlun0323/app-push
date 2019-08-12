package com.xz.msg.push.service.push.thirdparty.jpush;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月22日 下午2:25:28
 */
public abstract class JPushResponseCode {

	// 服务器端内部逻辑错误，请稍后重试。
	public static final Integer PUSH_SYSTEM_INTERNAL_ERROR = 1000;
	public static final Integer DEVICE_SYSTEM_INTERNAL_ERROR = 7000;

	// 内部服务超时，稍后重试
	public static final Integer INTERNAL_SERVICE_TIMEOUT = 1030;
	
	// API调用频率超出该应用的限制
	public static final Integer INVOKE_RATE_LIMIT = 2002;

	public static final List<Integer> NEED_RETRY_RESPONSE_CODE = new ArrayList<>();

	static {
		NEED_RETRY_RESPONSE_CODE.add(INVOKE_RATE_LIMIT);
		NEED_RETRY_RESPONSE_CODE.add(PUSH_SYSTEM_INTERNAL_ERROR);
		NEED_RETRY_RESPONSE_CODE.add(DEVICE_SYSTEM_INTERNAL_ERROR);
		NEED_RETRY_RESPONSE_CODE.add(INTERNAL_SERVICE_TIMEOUT);
	}
}
