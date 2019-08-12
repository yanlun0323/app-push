package com.xz.msg.push.service.push.thirdparty.jpush;

import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.utils.CacheUtils;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月22日 上午11:58:52
 */
public abstract class JPushCommonConfig {

	public static ClientConfig getClientConfig() {
		ClientConfig config = ClientConfig.getInstance();
		config.setMaxRetryTimes(5);
		config.setConnectionTimeout(10 * 1000); // 10 seconds

		return config;
	}
	
	/**
	 * jpushClient实例获取
	 * @param jmessage
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static JPushClient getJPushClientInstance(JPushMessageBuilder jmessage) {
		JPushClient jPushClient = null;
		if (CacheUtils.containsKey(jmessage.getAppKey())) {
			jPushClient = (JPushClient) CacheUtils.get(jmessage.getAppKey());
		} else {
			jPushClient = new JPushClient(jmessage.getMasterSecret(), jmessage.getAppKey(), null,
					JPushCommonConfig.getClientConfig());
			CacheUtils.put(jmessage.getAppKey(), jPushClient);
		}
		return jPushClient;
	}
}
