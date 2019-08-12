package com.xz.msg.push.service.push.thirdparty.jpush;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.exception.CommonErrCode;
import com.xz.msg.push.exception.CommonException;
import com.xz.msg.push.utils.CollectionUtils;

import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.PushPayload.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/** 
 * @author  作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月23日 下午4:21:25 
 */
public abstract class JPushBuilder {
	
	/**
	 * 构建PushPayload实例
	 * @param builder
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static List<PushPayload> buildPushPayload(JPushMessageBuilder builder, Map<String, Integer> keywordsMax) {
		List<PushPayload> pushPayloads = new ArrayList<>();
		
		switch (builder.getType()) {
		case JPushConstants.NOTIFICATION:
			pushPayloads = JPushBuilder.buildNotificationPushPayload(builder, keywordsMax);
			break;
		case JPushConstants.MESSAGE:
			pushPayloads = JPushBuilder.buildMessagePushPayload(builder, keywordsMax);
			break;
		default:
			throw new CommonException(CommonErrCode.BUSINESS, "未定义的消息类型");
		}

		return pushPayloads;
	}
	
	/**
	 * 构建通知类型的PushPayload实例
	 * @param builder
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static List<PushPayload> buildNotificationPushPayload(JPushMessageBuilder builder,
			Map<String, Integer> keywordsMax) {
		
		// 根据registrationId/tags/alias允许的最大数自主拆分，返回多个PushPayload
		List<PushPayload> pushPayloads = new ArrayList<>();
		JPushBuilder.smartSplitAudience(builder, keywordsMax).forEach(audience -> {
			// 全平台推送
			Builder jPushBuilder = PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(audience)
					.setNotification(buildAllPlatformNotification(builder));

			// 透传消息设置
			if (StringUtils.hasText(builder.getTransparentTransmissionContent())) {
				jPushBuilder.setMessage(Message.newBuilder().setTitle(builder.getTitle())
						.setMsgContent(builder.getTransparentTransmissionContent()).build());
			}
			
			PushPayload payload = jPushBuilder
					.setOptions(Options.newBuilder().setApnsProduction(builder.getApnsProduction()).build()).build();
			pushPayloads.add(payload);
		});

		return pushPayloads;
	}

	/**
	 * 全平台notification构建
	 * @param builder
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private static Notification buildAllPlatformNotification(JPushMessageBuilder builder) {
		// notification定义
		cn.jpush.api.push.model.notification.AndroidNotification.Builder androidBuilder = AndroidNotification
				.newBuilder().setAlert(builder.getMessage()).setTitle(builder.getTitle());
		
		cn.jpush.api.push.model.notification.IosNotification.Builder iosBuilder = IosNotification.newBuilder()
				.setAlert(builder.getMessage());
		
		// extras setting
		if (StringUtils.hasText(builder.getTransparentTransmissionContent())) {
			androidBuilder.addExtra(JPushConstants.EXTRAS_KEY, builder.getTransparentTransmissionContent());
			iosBuilder.addExtra(JPushConstants.EXTRAS_KEY, builder.getTransparentTransmissionContent());
		}
		
		Notification notification = Notification.newBuilder().addPlatformNotification(androidBuilder.build())
				.addPlatformNotification(iosBuilder.build()).build();
		return notification;
	}
	
	/**
	 * 构建消息类型的实例
	 * @param builder
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static List<PushPayload> buildMessagePushPayload(JPushMessageBuilder builder, Map<String, Integer> keywordsMax) {
		// 根据registrationId/tags/alias允许的最大数自主拆分，返回多个PushPayload
		List<PushPayload> pushPayloads = new ArrayList<>();
		JPushBuilder.smartSplitAudience(builder, keywordsMax).forEach(audience -> {
			// 全平台推送
			PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(audience)
					.setMessage(Message.newBuilder().setTitle(builder.getTitle())
					.setMsgContent(builder.getMessage()).build())
					.setOptions(Options.newBuilder().setApnsProduction(builder.getApnsProduction()).build())
					.build();
			pushPayloads.add(payload);
		});
		return pushPayloads;
	}
	
	/**
	 * 根据registrationId/tags/alias允许的最大数自主拆分Audience
	 * @param builder
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static List<Audience> smartSplitAudience(JPushMessageBuilder builder, Map<String, Integer> keywordsMax) {
		List<Audience> target = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(builder.getRegistrationIds())) {
			splitRegistrationId(builder, keywordsMax, target);
		} else if (CollectionUtils.isNotEmpty(builder.getAlias())) {
			splitAlias(builder, keywordsMax, target);
		} else if (CollectionUtils.isNotEmpty(builder.getTags())) {
			splitTags(builder, keywordsMax, target);
		}
		return target;
	}

	/**
	 * 达到阙值拆分tags
	 * @param builder
	 * @param keywordsMax
	 * @param target
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private static void splitTags(JPushMessageBuilder builder, Map<String, Integer> keywordsMax,
			List<Audience> target) {
		Integer keywordTagMax = keywordsMax.get(JPushConstants.KEYWORD_TAG_MAX_KEY);
		if (builder.getTags().size() > keywordTagMax) {
			// 执行拆分任务
			CollectionUtils.split(builder.getTags(), keywordTagMax).forEach(subset -> {
				target.add(Audience.tag(subset));
			});
		} else {
			target.add(Audience.tag(builder.getTags()));
		}
	}

	/**
	 * 达到阙值拆分alias
	 * @param builder
	 * @param keywordsMax
	 * @param target
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private static void splitAlias(JPushMessageBuilder builder, Map<String, Integer> keywordsMax,
			List<Audience> target) {
		Integer keywordAliasMax = keywordsMax.get(JPushConstants.KEYWORD_ALIAS_MAX_KEY);
		if (builder.getAlias().size() > keywordAliasMax) {
			// 执行拆分任务
			CollectionUtils.split(builder.getAlias(), keywordAliasMax).forEach(subset -> {
				target.add(Audience.alias(subset));
			});
		} else {
			target.add(Audience.alias(builder.getAlias()));
		}
	}

	/**
	 * 达到阙值拆分registrationId
	 * @param builder
	 * @param keywordsMax
	 * @param target
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	private static void splitRegistrationId(JPushMessageBuilder builder, Map<String, Integer> keywordsMax,
			List<Audience> target) {
		Integer keywordRegistrationIdMax = keywordsMax.get(JPushConstants.KEYWORD_REGISTRATIONID_MAX_KEY);
		if (builder.getRegistrationIds().size() > keywordRegistrationIdMax) {
			// 执行拆分任务
			CollectionUtils.split(builder.getRegistrationIds(), keywordRegistrationIdMax).forEach(subset -> {
				target.add(Audience.registrationId(subset));
			});
		} else {
			target.add(Audience.registrationId(builder.getRegistrationIds()));
		}
	}
}
