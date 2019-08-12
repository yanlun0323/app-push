package com.xz.msg.push.sdk;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.xz.msg.push.sdk.entity.Messager;
import com.xz.msg.push.sdk.entity.PushResult;
import com.xz.msg.push.sdk.utils.CollectionUtils;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月23日 下午6:36:26
 */
public class MessagePushClient {

	private static CloseableHttpClient httpClient = HttpClients.createDefault();

	private static final String PUSH_MSG_SERVICE_URL = "/msg/push";
	private static final String PUSH_DEVICE_BIND_URL = "/push/device/";

	private String serverURL;
	private String appKey;
	private String appSecret;

	public MessagePushClient(String serverURL, String appKey, String appSecret) {
		this.serverURL = serverURL;
		this.appKey = appKey;
		this.appSecret = appSecret;
	}

	public PushResult pushMsg(Messager messager) throws IOException {
		String requestURL = serverURL + PUSH_MSG_SERVICE_URL;
		return execute(requestURL, messager);
	}

	/**
	 * 绑定Tag和Alias，方便後期通知
	 * 
	 * @param messager
	 * @return
	 * @throws IOException
	 * @author 作者:Yan,Email:yanlun0323@163.com
	 */
	public PushResult bindTagAndAlias(Messager messager) throws IOException {
		String requestURL = serverURL + PUSH_DEVICE_BIND_URL + CollectionUtils.findFirst(messager.getRegistrationIds());
		return execute(requestURL, messager);
	}

	private PushResult execute(String url, Messager messager) throws IOException, ClientProtocolException {
		HttpPost httpPost = new HttpPost(url);

		String body = JSON.toJSONString(messager);
		String md5 = DigestUtils.md5Hex(appSecret + messager.getMessage());

		httpPost.addHeader("auth-appkey", appKey);
		httpPost.addHeader("auth-md5", md5);

		StringEntity entity = new StringEntity(body, Charset.forName("utf-8"));
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return JSON.parseObject(EntityUtils.toString(response.getEntity()), PushResult.class);
			} else {
				return PushResult.fail("http响应->" + EntityUtils.toString(response.getEntity()));
			}
		} finally {
			response.close();
		}
	}
}
