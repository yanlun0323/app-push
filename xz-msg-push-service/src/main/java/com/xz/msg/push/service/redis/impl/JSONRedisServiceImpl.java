package com.xz.msg.push.service.redis.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import com.xz.msg.push.service.redis.RedisService;
import com.xz.msg.push.service.redis.impl.serializer.FastJsonRedisSerializer;

/**
 * 
 * @author Yan
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component("JSONRedisCache")
public class JSONRedisServiceImpl implements RedisService {

	private RedisTemplate<Serializable, Serializable> redisTemplate;

	@Autowired
	public JSONRedisServiceImpl(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public RedisTemplate<Serializable, Serializable> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(
			RedisTemplate<Serializable, Serializable> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public <T> void save(final String key, final T obj) {
		this.save(key, obj, null);
	}

	@Override
	public <T>  void save(final String key, final T obj, final Long expires) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(obj.getClass());
				byte[] k = serializer.serialize(key);
				if(expires != null && expires > 0 ) {
					connection.set(k, serializer.serialize(obj)
							, Expiration.seconds(expires), RedisStringCommands.SetOption.UPSERT);
				} else {
					connection.set(k, serializer.serialize(obj));
				}
				return null;
			}
		});
	}

	@Override
	public <T> void delete(final String key, final Class<T> type) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(type);
				byte[] k = serializer.serialize(key);
				connection.expire(k, 0);
				return null;
			}
		});
	}

	@Override
	public <T> T read(final String key,final Class<T> type) {
		return redisTemplate.execute(new RedisCallback<T>() {
			@Override
			public T doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(type);
				byte[] k = serializer.serialize(key);
				if (connection.exists(k)) {
					return (T) serializer.deserialize(connection.get(k));
				}
				return null;
			}
		});
	}


	@Override
	public <T> void pushSetItem(final String key, final T obj) {
		this.pushSetItem(key, obj, null);
	}

	@Override
	public <T> void pushSetItem(final String key, final T obj, final Long expires) {
		redisTemplate.execute(new RedisCallback<Object>(){
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(obj.getClass());
				byte[] k = serializer.serialize(key);
				byte[] v = serializer.serialize(obj);
				if(!connection.sIsMember(k, v)){
					connection.sAdd(k, v);
				}
				if(expires != null && expires > 0 ){
					connection.expire(k, expires);
				}
				return null;
			}
		});
		
	}

	@Override
	public <T> Set<T> readSet(final String key,final Class<T> type) {
		return redisTemplate.execute(new RedisCallback<Set<T>>() {
			@Override
			public Set<T> doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(type);
				byte[] k = serializer.serialize(key);
				if (connection.exists(k)) {
					Set<byte[]> value = connection.sMembers(k);
					Set<T> rst = new HashSet<T>();
					for(byte[] b: value){
						rst.add((T) serializer.deserialize(b));
					}
					return rst;
				}
				return null;
			}
		});
	}

	@Override
	public <T> void removeSetItem(final String key, final T obj) {
		redisTemplate.execute(new RedisCallback<Object>(){
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(obj.getClass());
				byte[] k = serializer.serialize(key);
				byte[] v = serializer.serialize(obj);
				if(!connection.sIsMember(k, v)){
					connection.sRem(k, v);
				}
				return null;
			}
		});
	}

	@Override
	public <T> void pushListItem(final String key, final T obj) {
		this.pushListItem(key, obj, null);
	}

	@Override
	public <T> void pushListItem(final String key, final T obj, final Long expires) {
		redisTemplate.execute(new RedisCallback<Object>(){
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(obj.getClass());
				byte[] k = serializer.serialize(key);
				byte[] v = serializer.serialize(obj);
					connection.rPush(k, v);
				if(expires != null && expires > 0 ){
					connection.expire(k, expires);
				}
				return null;
			}
		});
	}

	@Override
	public <T> List<T> bRPopItem(String key, Class<T> type, Integer timeout) {
		return redisTemplate.execute(new RedisCallback<List<T>>(){
			@Override
			public List<T> doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(type);
				byte[] keys = serializer.serialize(key);
				if (connection.exists(keys)) {
					List<byte[]> value = connection.bRPop(timeout, keys);
                    List<T> values = new ArrayList<T>(value.size());
                    for (byte[] bs : value) {
                        values.add((T) serializer.deserialize(bs));
                    }
                    return values;
				}
				return new ArrayList<>();
			}
		});
	}

	@Override
	public <T> void removeListItem(final String key, final T obj) {
		redisTemplate.execute(new RedisCallback<Object>(){
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(obj.getClass());
				byte[] k = serializer.serialize(key);
				byte[] v = serializer.serialize(obj);
				connection.lRem(k,1, v);
				return null;
			}
		});
	}

	@Override
	public <T> List<T> readList(final String key,final Class<T> type) {
		return redisTemplate.execute(new RedisCallback<List<T>>() {
			@Override
			public List<T> doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(type);
				byte[] k = serializer.serialize(key);
				if (connection.exists(k)) {
					List<byte[]> value = connection.lRange(k, 0, -1);
                    List<T> values = new ArrayList<T>(value.size());
                    for (byte[] bs : value) {
                        values.add((T) serializer.deserialize(bs));  
                    }
                    return values;
				}
				return null;
			}
		});
	}

	@Override
	public Long incr(final String key) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Long.class);
				byte[] k = serializer.serialize(key);
				return connection.incr(k);
			}
		});
	}

	@Override
	public Long incrBy(final String key, final Long integer) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection)
					throws DataAccessException {
				FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Long.class);
				byte[] k = serializer.serialize(key);
				return connection.incrBy(k, integer);
			}
		});
	}

	@Override
	public RedisSerializer<?> getRedisSerializer() {
		return null;
	}
}
