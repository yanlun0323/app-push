package com.xz.msg.push.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xz.msg.push.entity.JPushConfig;
import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.entity.Result;
import com.xz.msg.push.exception.CommonErrCode;
import com.xz.msg.push.exception.CommonException;
import com.xz.msg.push.service.JPushConfigService;
import com.xz.msg.push.service.push.PushService;
import com.xz.msg.push.utils.CollectionUtils;
import com.xz.msg.push.utils.RedisKeyConfigure;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月18日 下午2:03:46
 */
@RestController
@RequestMapping("/msg")
public class MsgPushController {

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private JPushConfigService jPushConfigService;
	@Autowired
	private PushService pushService;

	private Log logger = LogFactory.getLog(MsgPushController.class);

	@RequestMapping(value = "/push", method = RequestMethod.POST)
	public ResponseEntity<Result> push(@RequestBody String message, HttpServletRequest request) {
		logger.debug(message);
		
		String authAppKey = request.getHeader("auth-appkey");
		String authMd5 = request.getHeader("auth-md5");
		
		JPushMessageBuilder messageBuilder = JSON.parseObject(message, JPushMessageBuilder.class);
		
		if (StringUtils.isEmpty(authAppKey) || StringUtils.isEmpty(authMd5)) {
			return new ResponseEntity<Result>(Result.fail("认证信息未填写"), HttpStatus.BAD_REQUEST);
		} else if (!validAuthentication(messageBuilder.getMessage(), authAppKey, authMd5)) {
			return new ResponseEntity<Result>(Result.fail("认证失败"), HttpStatus.BAD_REQUEST);
		}

		this.executePushAction(authAppKey, messageBuilder);

		return new ResponseEntity<Result>(Result.success("推送成功"), HttpStatus.OK);
	}

	@RequestMapping(value = "/batch/push", method = RequestMethod.POST)
	public ResponseEntity<Result> batchPush(@RequestBody String message, HttpServletRequest request) {
		logger.debug(message);

		List<JPushMessageBuilder> builders = JSON.parseArray(message, JPushMessageBuilder.class);
		if (CollectionUtils.isNotEmpty(builders)) {
			// 执行认证
			String authAppKey = request.getHeader("auth-appkey");
			String authMd5 = request.getHeader("auth-md5");
			if (StringUtils.isEmpty(authAppKey) || StringUtils.isEmpty(authMd5)) {
				return new ResponseEntity<Result>(Result.fail("认证信息未填写"), HttpStatus.BAD_REQUEST);
			} else if (!validAuthentication(message, authAppKey, authMd5)) {
				return new ResponseEntity<Result>(Result.fail("认证失败"), HttpStatus.BAD_REQUEST);
			}
			
			logger.debug(JSON.toJSONString(builders));
			
			builders.stream().forEach(messager -> this.executePushAction(authAppKey, messager));
		}
		return new ResponseEntity<Result>(Result.success("推送成功"), HttpStatus.OK);
	}
	
	private void executePushAction(String authAppKey, JPushMessageBuilder messageBuilder) {
		//MSG 推送设备对象AudienceTarget校验
		this.validateMsg(authAppKey, messageBuilder);
		//异步通知
		taskExecutor.submit(() -> {
			JPushConfig config = this.jPushConfigService.findJPushConfig(authAppKey);
			messageBuilder.setAppKey(config.getAppKey());
			messageBuilder.setMasterSecret(config.getMasterSecret());
			
			List<String> queueKeys = this.jPushConfigService.findAppTopic(authAppKey);
			
			pushService.publish(messageBuilder, RedisKeyConfigure.REDIS_PATTERN_TOPIC, queueKeys);
		});
	}

	/**
	 * app认证
	 * 
	 * @return
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	public boolean validAuthentication(String digestBody, String authAppKey, String authMd5) {
		String appSecret = this.jPushConfigService.findAppSecret(authAppKey);
		if (StringUtils.isEmpty(appSecret)) {
			return Boolean.FALSE;
		} else {
			StringBuilder md5Digest = new StringBuilder(appSecret);
			md5Digest.append(digestBody);
			return DigestUtils.md5Hex(md5Digest.toString()).equals(authMd5);
		}
	}
	
	/**
	 * 验证请求消息
	 * 
	 * @param msg
	 * @throws Exception
	 */
	private void validateMsg(String authAppKey,JPushMessageBuilder builder) {
		if (StringUtils.isEmpty(authAppKey)) {
			throw new CommonException(CommonErrCode.BUSINESS, "应用名称不能为空");
		}
		if (!jPushConfigService.isValidAppName(authAppKey)) {
			throw new CommonException(CommonErrCode.BUSINESS, "应用名称不正确");
		}
		this.validateTarget(builder);
	}

	/**
	 * 验证目标
	 */
	private void validateTarget(JPushMessageBuilder builder) {
		int mark = 0;
		// 目标只能存在一个
		mark = (builder.getRegistrationIds() == null) ? mark : mark + 1;
		mark = (builder.getTags() == null) ? mark : mark + 2;
		mark = (builder.getAlias() == null) ? mark : mark + 4;

		if (mark != 1 && mark != 2 && mark != 4) {
			throw new CommonException(CommonErrCode.BUSINESS, "只能提交一个目标类型");
		}
	}
}
