package com.xz.msg.push.utils;

/**
 * 
 * @author Yan
 *
 */
public class RedisKeyConfigure {
	
	public static final String ERROR_QUEUE_PREFIX = "PUSH_ERROR_";
	public static final String REDIS_PATTERN_TOPIC = "PUSH_TOPIC";
	
	public static final String AJIA_EDU_TOPIC_KEY = "PUSH_TOPIC_WITH_AJIA_EDU";
	public static final String MIDDLE_TEACHER_TOPIC_KEY = "PUSH_TOPIC_WITH_MIDDLE_TEACHER";
	public static final String PRIMARY_PARENT_TOPIC_KEY = "PUSH_TOPIC_WITH_PRIMARY_PARENT";
	public static final String PRIMARY_TEACHER_TOPIC_KEY = "PUSH_TOPIC_WITH_PRIMARY_TEACHER";

	public static final String RATE_LIMIT_RESET_PREFIX = "RATE_LIMIT_RESET";
	public static final String REQUEST_TOO_FAST_PREFIX = "REQUEST_TOO_FAST_PREFIX";
	
	public static String RateLimitRestCacheKey(String appKey){
		return Key.with(RATE_LIMIT_RESET_PREFIX).append("APP_KEY").append(appKey).string();
	}
	
	public static String RequestTooFastCacheKey(String appKey){
		return Key.with(REQUEST_TOO_FAST_PREFIX).append("APP_KEY").append(appKey).string();
	}
	
	public static class Key {
		private StringBuilder builder;

		private Key() {
			this.builder = new StringBuilder();
		}

		private Key(String key) {
			this();
			this.builder.append(key);
		}

		public static Key with(String key) {
			return new Key(key);
		}

		public Key append(Object key) {
			builder.append(":").append(key);
			return this;
		}

		public String string() {
			return this.toString();
		}

		@Override
		public String toString() {
			return this.builder.toString();
		}
	}

}
