package com.xz.msg.push.service.push;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.xz.msg.push.entity.JPushMessageBuilder;
import com.xz.msg.push.entity.MessageBuilder;
import com.xz.msg.push.utils.CollectionUtils;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月22日 下午5:28:05
 */
public class ThreadTest {
	
	public static void invoke(MessageBuilder message){
		JPushMessageBuilder jmessage = (JPushMessageBuilder) message;
		System.out.println(jmessage.getAlias());
	}

	public static void main(String[] args) {
		JPushMessageBuilder jmessage = new JPushMessageBuilder();
		jmessage.setAlias(CollectionUtils.buildHashSet("Coder.Yan"));
		
		invoke(jmessage);
		
		for (int i = 0; i < 200; i++) {
			ReentrantLock lock = new ReentrantLock();
			lock.lock();
			try {
				// 判断是否达到推送频率上线，如果是则需要等待RateLimitReset对应的时间；
				// 如果推送数量短时间内达到80%，则线程等待N秒
				if(i == 100){
					lock.tryLock(2, TimeUnit.SECONDS);
					System.out.println("sleep");
				}
				
				System.out.println(i);
			} catch (InterruptedException e) {
			} finally {
				lock.unlock();
			}
		}
	}
}
