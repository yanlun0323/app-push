package com.xz.msg.push.sdk.entity;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;

import com.xz.msg.push.sdk.constants.Constants;
import com.xz.msg.push.sdk.utils.CollectionUtils;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月18日 上午10:05:42
 */
public class MessageBuilder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8609426504391926732L;

	private Messager messager;

	private int buildNotificationCounter;
	private int buildMessageCounter;
	private int buildTargetCounter;
	private int buildDeviceBindTargetCounter;

	public MessageBuilder() {
		messager = new Messager();
	}
	
	/**
	 * 透传消息构建（应用内消息）
	 * @param title
	 * @param message
	 * @return
	 * @throws Exception
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public MessageBuilder buildMessage(String title, String message) throws Exception {
		if (buildMessageCounter == 0) {
			messager.setTitle(title);
			messager.setMessage(message);
			messager.setType(Constants.MESSAGE);
			buildMessageCounter++;
			return this;
		} else {
			throw new Exception("只能构建一次透传消息[Message]");
		}
	}

	/**
	 * 通知构建
	 * @param title
	 * @param message
	 * @return
	 * @throws Exception
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public MessageBuilder buildNotification(String title, String message) throws Exception {
		return this.buildNotification(title, message, null);
	}
	
	/**
	 * 通知构建（若transparentTransmissionContent不为空则同时推送透传消息）
	 * 
	 * @param title
	 * @param message
	 * @param transparentTransmissionContent 透传消息内容
	 * @return
	 * @throws Exception
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public MessageBuilder buildNotification(String title, String message, String transparentTransmissionContent) throws Exception {
		if (buildNotificationCounter == 0) {
			messager.setTitle(title);
			messager.setMessage(message);
			messager.setType(Constants.NOTIFICATION);
			messager.setTransparentTransmissionContent(transparentTransmissionContent);
			buildNotificationCounter++;
			return this;
		} else {
			throw new Exception("只能构建一次通知[Notification]");
		}
	}

	/**
	 * 设备绑定目标构建
	 * 
	 * @param registrationId
	 * @param alias
	 * @param tags
	 * @return
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 * @throws Exception
	 */
	public MessageBuilder buildDeviceBindTarget(String registrationId, String alias, String ...tags)
			throws Exception {
		
		Asserts.check(StringUtils.isNotEmpty(registrationId), "registrationId不能为空");
		
		if (buildDeviceBindTargetCounter == 0) {
			messager.setRegistrationIds(CollectionUtils.buildHashSet(registrationId));
			
			if(StringUtils.isNotBlank(alias)){
				messager.setAlias(CollectionUtils.buildHashSet(alias));
			}
			
			Set<String> tagSet = CollectionUtils.buildHashSet(tags);
			if(CollectionUtils.isNotEmpty(tagSet)){
				messager.setTags(tagSet);
			}
		} else {
			throw new Exception("只能构建一次设备绑定对象[DeviceBindTarget]");
		}
		return this;
	}

	/**
	 * 通知目标设备构建
	 * @param type
	 * @param targets
	 * @return
	 * @throws Exception
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public MessageBuilder buildTarget(String type, String... targets) throws Exception {
		if (buildTargetCounter == 0) {
			switch (type) {
			case MessagerTarget.REGISTRATIONIDS:
				messager.setRegistrationIds(CollectionUtils.buildHashSet(targets));
				break;
			case MessagerTarget.ALIAS:
				messager.setAlias(CollectionUtils.buildHashSet(targets));
				break;
			case MessagerTarget.TAGS:
				messager.setTags(CollectionUtils.buildHashSet(targets));
				break;
			default:
				throw new Exception("不支持的目标类型[" + type + "]");
			}
			buildTargetCounter++;
			return this;
		} else {
			throw new Exception("只能构建一次Targets");
		}
	}

	public Messager build() {
		return messager;
	}
}
