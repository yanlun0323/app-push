package com.xz.msg.push.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.xz.msg.push.utils.RedisKeyConfigure;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月17日 下午10:01:39
 */
@Configuration
public class CommonConfig {

	/**
	 * ThreadPoolTaskExecutor 定制
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
		// 线程池所使用的缓冲队列
		poolTaskExecutor.setQueueCapacity(200);
		// 线程池维护线程的最少数量
		poolTaskExecutor.setCorePoolSize(5);
		// 线程池维护线程的最大数量
		poolTaskExecutor.setMaxPoolSize(50);
		// 线程池维护线程所允许的空闲时间
		poolTaskExecutor.setKeepAliveSeconds(30000);
		poolTaskExecutor.initialize();
		return poolTaskExecutor;
	}
	
	
	/**
	 * Redis MessageListener
	 * @param connectionFactory
	 * @param listeners
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListener... listeners) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();

		container.setConnectionFactory(connectionFactory);

		PatternTopic topics = new PatternTopic(RedisKeyConfigure.REDIS_PATTERN_TOPIC);
		for (MessageListener listener : listeners) {
			container.addMessageListener(listener, topics);
		}

		return container;
	}
}
