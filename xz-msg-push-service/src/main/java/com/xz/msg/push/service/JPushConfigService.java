package com.xz.msg.push.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xz.msg.push.entity.JPushConfig;
import com.xz.msg.push.entity.User;

@Service
public class JPushConfigService {

	private static final Log log = LogFactory.getLog(JPushConfigService.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	private Map<String, JPushConfig> configer = new HashMap<>();
	private Map<String, User> users = new HashMap<>();

	@PostConstruct
	public void init() {
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true); // 允许没有引号的key字段
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@PostConstruct
	public void usersInit() {
		try {
			String userConfiger = IOUtils
					.toString(this.getClass().getClassLoader().getResourceAsStream("jpush.user.config"));

			log.info(userConfiger);

			User[] jpushUsers = objectMapper.readValue(userConfiger, User[].class);
			for (User vo : jpushUsers) {
				users.put(vo.getAppkey(), vo);
			}
			log.info("读取[jpush.user.config]文件成功，用户数:" + users.size());
		} catch (Exception e) {
			log.error("读取[jpush.user.config]文件失败", e);
		}
	}

	@PostConstruct
	public void configerInit() {
		try {
			String pushConfiger = IOUtils
					.toString(this.getClass().getClassLoader().getResourceAsStream("jpush.config"));

			log.info(pushConfiger);

			JPushConfig[] jpushConfiger = objectMapper.readValue(pushConfiger, JPushConfig[].class);
			for (JPushConfig vo : jpushConfiger) {
				configer.put(vo.getName(), vo);
			}
			log.info("读取[jpush.config]文件成功，size:" + configer.size());
		} catch (Exception e) {
			log.error("读取[jpush.config]文件失败", e);
		}
	}

	/**
	 * 根据Key 查找 secret 值
	 * 
	 * @param key
	 * @return
	 */
	public String findAppSecret(String key) {
		return users.containsKey(key) ? users.get(key).getAppsecret() : null;
	}

	public List<String> findAppTopic(String key) {
		return users.containsKey(key) ? users.get(key).getTopic() : null;
	}

	public JPushConfig findJPushConfig(String key) {
		return configer.containsKey(key) ? configer.get(key) : null;
	}

	/**
	 * 自身的APP調用驗證規則
	 * 
	 * @param appName
	 * @return
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	public boolean isValidAppName(String appName) {
		return configer.containsKey(appName);
	}
}
