package com.xz.msg.push.service.push.thirdparty;

import com.xz.msg.push.entity.MessageBuilder;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月19日 下午2:53:37
 */
public interface ThirdPartyPushService {

	/**
	 * 当达到指定频率后自动等待，挂起线程
	 * 
	 * @param messageBuilder
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	public void smartInvoke(MessageBuilder messageBuilder) throws Exception;
	
	/**
	 * eg:根据Registration ID标识，设置alias或tags
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public void setAliasAndTags(MessageBuilder message);
}
