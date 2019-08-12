package com.xz.msg.push.utils.cache;


import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
/**
 *  * 本地缓存，不使用锁，最坏的结果是多线程导致重复put和delete，由ConcurrentHashMap保证一致性
 * <p>
 * 不使用线程定期清理，而是在put时查看是否超出设定容量，然后进行清理；在get时，查看是否超时，进行清理
 * 
 * @author Yan
 *
 * @param <T>
 */
public class LocalCache<T> {

	/**
	 * 表明key对应的值为空
	 */
	public static final String EMPTY = "_EP";

	/**
	 * 默认本地缓存的时间长度，单位：毫秒
	 */
	private static final Long LOCAL_EXPIRE_TIME = 60 * 1000L;

	/**
	 * 超时时间，单位：毫秒，负数表示永不超时
	 */
	private Long expireTime = LOCAL_EXPIRE_TIME;

	/**
	 * 默认本地缓存的最大条数
	 */
	private static final Long MAX_LOCAL_CAPACITY = 1000 * 10000L;

	private Long capacity = MAX_LOCAL_CAPACITY;

	private Map<String, Cacheable<T>> localCaches = new ConcurrentHashMap<String, Cacheable<T>>();

	public LocalCache() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param expireTime
	 *            超时时间，单位：毫秒，负数表示永不超时
	 * @param capacity
	 *            本地缓存的最大条数
	 */
	public LocalCache(Long expireTime, Long capacity) {
		super();
		this.expireTime = expireTime;
		this.capacity = capacity;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public Long getCapacity() {
		return capacity;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
	}

	private void clearAndCheck() throws IllegalAccessException {
		if (localCaches.size() >= capacity) {
			for (Entry<String, Cacheable<T>> entry : localCaches.entrySet()) {
				Cacheable<T> cacheable = entry.getValue();
				if (null != cacheable
						&& System.currentTimeMillis() > cacheable
								.getExpiredTimestamp()) {
					localCaches.remove(entry.getKey());
				}
			}
		}

		if (localCaches.size() >= capacity) {
			throw new IllegalAccessException("the size reached capacity:"
					+ capacity);
		}
	}

	public void put(String key, T value) throws IllegalAccessException {
		put(key, value, expireTime);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 *            超时时间，单位：毫秒，负数表示永不超时
	 * @throws IllegalAccessException
	 */
	public void put(String key, T value, Long timeout)
			throws IllegalAccessException {
		clearAndCheck();
		Cacheable<T> cacheable = new Cacheable<T>(value);
		if (0 <= timeout) {	
			cacheable.setExpiredTimestamp(System.currentTimeMillis() + timeout);
		} else {
			cacheable.setExpiredTimestamp(Long.MAX_VALUE);
		}
		localCaches.put(key, cacheable);
	}

	public T get(String key) {
		Cacheable<T> cacheable = localCaches.get(key);
		if (null != cacheable) {
			Long expiredTime = cacheable.getExpiredTimestamp();
			if (System.currentTimeMillis() > expiredTime) {
				localCaches.remove(key);
				return null;
			}
			return cacheable.getValue();
		}
		return null;
	}

	public Boolean containsKey(String key){
		return localCaches.containsKey(key);
	}
	
	public void delete(String key) {
		if (null != key) {
			localCaches.remove(key);
		}
	}


}
