package com.xz.msg.push.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xz.msg.push.entity.JPushConfig;
import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.entity.MessageBuilder;
import com.xz.msg.push.entity.Result;
import com.xz.msg.push.service.JPushConfigService;
import com.xz.msg.push.service.push.thirdparty.ThirdPartyPushService;
import com.xz.msg.push.utils.CheckUtils;
import com.xz.msg.push.utils.CollectionUtils;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月18日 下午2:03:46
 */
@RestController
@RequestMapping("/push")
public class UpdateDeviceTagAliasController extends BaseController {

	@Value("${jpush.push.key.max.length}")
	private Integer maxLength;

	@Autowired
	private JPushConfigService jPushConfigService;
	@Resource(name = "JPushService")
	private ThirdPartyPushService thirdPartyPushService;

	private Log logger = LogFactory.getLog(UpdateDeviceTagAliasController.class);

	@RequestMapping(value = "/device/{registrationId}", method = RequestMethod.POST)
	public ResponseEntity<Result> push(@RequestBody String message,
			@PathVariable("registrationId") String registrationId, HttpServletRequest request) {

		String authAppKey = request.getHeader("auth-appkey");
		String authMd5 = request.getHeader("auth-md5");

		JPushMessageBuilder builder = JSON.parseObject(message, JPushMessageBuilder.class);
		if (StringUtils.isEmpty(authAppKey) || StringUtils.isEmpty(authMd5)) {
			return new ResponseEntity<Result>(Result.fail("认证信息未填写"), HttpStatus.BAD_REQUEST);
		} else if (!validAuthentication(builder, authAppKey, authMd5)) {
			return new ResponseEntity<Result>(Result.fail("认证失败"), HttpStatus.BAD_REQUEST);
		}

		// tag和alias格式验证
		CheckUtils.validateTags(builder.getTags(), maxLength);
		CheckUtils.validateAlias(CollectionUtils.findFirst(builder.getAlias()), maxLength);

		logger.debug(message);

		builder.setRegistrationIds(CollectionUtils.buildHashSet(registrationId));

		JPushConfig config = this.jPushConfigService.findJPushConfig(authAppKey);
		builder.setAppKey(config.getAppKey());
		builder.setMasterSecret(config.getMasterSecret());

		//同步绑定（实际上由SDK调用者决定）
		thirdPartyPushService.setAliasAndTags(builder);

		return new ResponseEntity<Result>(Result.success("设置成功"), HttpStatus.OK);
	}

	/**
	 * app认证
	 * 
	 * @return
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	public boolean validAuthentication(MessageBuilder builder, String authAppKey, String authMd5) {
		String appSecret = this.jPushConfigService.findAppSecret(authAppKey);
		if (StringUtils.isEmpty(appSecret)) {
			return Boolean.FALSE;
		} else {
			StringBuilder md5Digest = new StringBuilder(appSecret);
			md5Digest.append(builder.getMessage());
			return DigestUtils.md5Hex(md5Digest.toString()).equals(authMd5);
		}
	}
}
