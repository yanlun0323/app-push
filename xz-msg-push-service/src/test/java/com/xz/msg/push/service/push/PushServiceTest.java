package com.xz.msg.push.service.push;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.xz.msg.push.entity.JPushConfig;
import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.service.BasicTestRunner;
import com.xz.msg.push.service.JPushConfigService;
import com.xz.msg.push.service.push.thirdparty.jpush.JPushConstants;
import com.xz.msg.push.utils.CollectionUtils;
import com.xz.msg.push.utils.RedisKeyConfigure;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月18日 上午11:16:01
 */
public class PushServiceTest extends BasicTestRunner {

	@Autowired
	private PushService pushService;
	@Autowired
	private JPushConfigService jPushConfigService;
	
	private static final Integer MAX = 600;

	private static final String AUTH_APP_KEY = "primaryTeacher";

	@Test
	public void test() throws Exception {

		/*
		 * for (int i = 1; i <= 5; i++) { MessageBuilder teacherMessage = new
		 * MessageBuilder();
		 * teacherMessage.setMessage(RedisKeyConfigure.MIDDLE_TEACHER_TOPIC_KEY
		 * + " Message->" + i); pushService.publish(teacherMessage,
		 * RedisKeyConfigure.REDIS_PATTERN_TOPIC,
		 * Arrays.asList(RedisKeyConfigure.MIDDLE_TEACHER_TOPIC_KEY));
		 * 
		 */

		for (int i = 1; i <= MAX; i++) {

			JPushMessageBuilder builder = new JPushMessageBuilder();
			
			JPushConfig config = this.jPushConfigService.findJPushConfig(AUTH_APP_KEY);
			builder.setAppKey(config.getAppKey());
			builder.setMasterSecret(config.getMasterSecret());
			builder.setTitle("Code.Yan' MsgContent");
			builder.setType(JPushConstants.MESSAGE);
			builder.setRegistrationIds(CollectionUtils.buildHashSet("1114a8979298d8a749b"));
			builder.setMessage(RedisKeyConfigure.PRIMARY_TEACHER_TOPIC_KEY + " Message->" + i);
			
			List<String> queueKeys = this.jPushConfigService.findAppTopic(AUTH_APP_KEY);
			
			Thread.sleep(100);
			
			pushService.publish(builder, RedisKeyConfigure.REDIS_PATTERN_TOPIC, queueKeys);
		}

		Thread.sleep(Integer.MAX_VALUE);
	}
}
