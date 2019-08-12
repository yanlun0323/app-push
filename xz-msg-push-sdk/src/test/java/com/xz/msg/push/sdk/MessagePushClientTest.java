package com.xz.msg.push.sdk;

import com.xz.msg.push.sdk.entity.MessageBuilder;
import com.xz.msg.push.sdk.entity.MessagerTarget;
import com.xz.msg.push.sdk.entity.PushResult;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月23日 下午6:36:26
 */
public class MessagePushClientTest {
	
	public static void localDebug(){
		//MessagePushClient client = new MessagePushClient("http://192.168.1.181:8698/", "primaryParent", "cJCMJYwTy5SuY0rP");
		MessagePushClient client = new MessagePushClient("http://localhost:8698/", "primaryParent", "cJCMJYwTy5SuY0rP");
		try {
			PushResult result = client.pushMsg(new MessageBuilder().buildNotification("Code.Yan", "Coder.Yan Msg Push", "{\"type\":\"newHomework\"}")
					.buildTarget(MessagerTarget.ALIAS, "Coder.Yan").build());

			System.out.println(result.getResultCode() + ", " + result.getMessage());

			// ---------------------------------------------------------------------------------------------------------//

			/*PushResult result2 = client.bindTagAndAlias(new MessageBuilder()
					.buildDeviceBindTarget("161a3797c8341b397d9", "9305252058")
					.build());
			System.out.println(result2.getResultCode() + ", " + result2.getMessage());*/
			
			
			PushResult result3 = client.pushMsg(new MessageBuilder().buildMessage("Code.Yan", "Coder.Yan Msg Push")
					.buildTarget(MessagerTarget.ALIAS, "Coder.Yan").build());

			System.out.println(result3.getResultCode() + ", " + result3.getMessage());
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void unionDebug(){
		MessagePushClient client = new MessagePushClient("http://192.168.1.181:8698/", "primaryParent", "cJCMJYwTy5SuY0rP");
		//MessagePushClient client = new MessagePushClient("http://localhost:8698/", "primaryParent", "cJCMJYwTy5SuY0rP");
		try {
			PushResult result = client.pushMsg(new MessageBuilder().buildNotification("Code.Yan", "Coder.Yan Msg Push")
					.buildTarget(MessagerTarget.REGISTRATIONIDS, "161a3797c8341b397d9").build());

			System.out.println(result.getResultCode() + ", " + result.getMessage());

			// ---------------------------------------------------------------------------------------------------------//

			/*PushResult result2 = client.bindTagAndAlias(new MessageBuilder()
					.buildDeviceBindTarget("161a3797c8341b397d9", "9305252058")
					.build());
			System.out.println(result2.getResultCode() + ", " + result2.getMessage());*/
			
			
			PushResult result3 = client.pushMsg(new MessageBuilder().buildMessage("Code.Yan", "Coder.Yan Msg Push")
					.buildTarget(MessagerTarget.REGISTRATIONIDS, "161a3797c8341b397d9").build());

			System.out.println(result3.getResultCode() + ", " + result3.getMessage());
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//联调模式
		//unionDebug();
		
		//本地调试模式
		localDebug();
	}
}
