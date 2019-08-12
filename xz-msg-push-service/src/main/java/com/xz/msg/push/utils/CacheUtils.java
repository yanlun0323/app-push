package com.xz.msg.push.utils;

import com.xz.msg.push.utils.cache.LocalCache;

/** 
 * @author  作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年6月12日 下午12:44:28 
 */
public abstract class CacheUtils {
	private static final LocalCache<Object> localCache = new LocalCache<Object>(-1L, 50L);

	public static Object get(String key){
		return localCache.get(key);
	}
	
	public static void put(String key, Object value){
		try {
			localCache.put(key, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean containsKey(String key){
		return localCache.containsKey(key);
	}
}
