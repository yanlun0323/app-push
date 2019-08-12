package com.xz.msg.push.service.redis.lock;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import com.xz.msg.push.exception.CommonErrCode;
import com.xz.msg.push.exception.CommonException;

/**
 * 
 * @author Yan
 *
 */
public class RedisDistributedLock {

	private static final String REDIS_LOCK_KEY_PREFIX = "REDIS_LOCK";

	private static final Long MIN_EXPIRY_IN_MILLISECONDS = 1000L;
	private static final Long MAX_EXPIRY_IN_MILLISECONDS = 15 * 60 * 1000L;

	private static final Logger logger = Logger.getLogger(RedisDistributedLock.class);

	private RedisTemplate<Serializable, Serializable> redisTemplate;

	private static final ThreadLocal<byte[]> redisKey = new ThreadLocal<>();
	private static final ThreadLocal<byte[]> redisValue = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> locked = new ThreadLocal<>();

	@SuppressWarnings("unused")
	private RedisDistributedLock() {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RedisDistributedLock(RedisTemplate redisTemplate, String key) {
		Assert.notNull(redisTemplate, "redisTemplate");
		Assert.hasText(key, "key");
		this.redisTemplate = redisTemplate;
		redisKey.set((REDIS_LOCK_KEY_PREFIX + ":" + key).getBytes());
		locked.set(Boolean.FALSE);
	}

	public boolean tryLock(Integer expiryInMillisecond) {
		if (expiryInMillisecond == null || expiryInMillisecond < MIN_EXPIRY_IN_MILLISECONDS) {
			throw new CommonException(CommonErrCode.ARGS_INVALID,
					"过期时间为空或设置过小[MIN_EXPIRY_IN_MILLISECONDS=" + MIN_EXPIRY_IN_MILLISECONDS + "]");
		}
		if (expiryInMillisecond > MAX_EXPIRY_IN_MILLISECONDS) {
			throw new CommonException(CommonErrCode.ARGS_INVALID,
					"过期时间为设置过大[MAX_EXPIRY_IN_MILLISECONDS=" + MAX_EXPIRY_IN_MILLISECONDS + "]");
		}
		if (this.isLocked())
			return Boolean.FALSE;
		// Try Lock
		RedisConnection connection = null;
		try {
			connection = this.redisTemplate.getConnectionFactory().getConnection();
			// Generate Lock Value
			StringBuilder builder = new StringBuilder(UUID.randomUUID().toString().replaceAll("-", ""));
			builder.append(":").append(System.currentTimeMillis());
			redisValue.set(builder.toString().getBytes());
			connection.pSetEx(redisKey.get(), expiryInMillisecond, redisValue.get());
			
			byte[] actualValue = connection.get(redisKey.get());
			if (actualValue != null && Arrays.equals(actualValue, redisValue.get())) {
				locked.set(true);
			}
		} catch (Throwable th) {
			logger.warn("<<<<<< Set Ex To Redis Fail(key=" + getKey() + ")", th);
			unlock();
		} finally {
			releaseConnection(connection);
		}
		return isLocked();
	}

	public void unlock() {
		RedisConnection connection = null;
		try {
			connection = this.redisTemplate.getConnectionFactory().getConnection();
			if (connection.exists(redisKey.get())) {
				byte[] lockValue = connection.get(redisKey.get());
				if (Arrays.equals(lockValue, redisValue.get())) {
					connection.del(redisKey.get());
				}
			}
			locked.set(false);
		} catch (Throwable th) {
			logger.error("<<<<<< Remove Redis Lock Key Fail(key=" + getKey() + ")", th);
		} finally {
			releaseConnection(connection);
		}
	}

	public boolean isLocked() {
		return locked.get();
	}

	public String getKey() {
		return new String(redisKey.get());
	}

	public void releaseConnection(RedisConnection connection) {
		if (connection != null) {
			connection.close();
		}
	}
}
