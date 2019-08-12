package com.xz.msg.push.service.redis.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import com.xz.msg.push.service.redis.RedisService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author Yan
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component("redisCache")
public class CustomRedisServiceImpl implements RedisService {

	private RedisTemplate<Serializable, Serializable> redisTemplate;

	private RedisSerializer defaultSerializer;

	@Autowired
	public CustomRedisServiceImpl(RedisTemplate redisTemplate) {
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

	@PostConstruct
	public void init() throws Exception {
		this.defaultSerializer = this.redisTemplate.getDefaultSerializer();
	}
	
	@Override
	public <T> void save(final String key, final T obj, final Long expires) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] k = defaultSerializer.serialize(key);

				if(expires != null && expires > 0 ){
					connection.setEx(k,expires,defaultSerializer.serialize(obj));
				}else{
					connection.set(k, defaultSerializer.serialize(obj));
				}
				return null;
			}
		});
	}

	@Override
	public <T> void save(final String key, final T obj) {
		this.save(key, obj, null);
	}

	public <T> T read(final String key) {
		return redisTemplate.execute(new RedisCallback<T>() {
			@Override
			public T doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] k = defaultSerializer.serialize(key);
				if (connection.exists(k)) {
					return (T) defaultSerializer.deserialize(connection.get(k));
				}
				return null;
			}
		});
	}

	public void delete(final String key) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] k = defaultSerializer.serialize(key);
				connection.expire(k, 0);
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
				byte[] k = defaultSerializer.serialize(key);
				byte[] v = defaultSerializer.serialize(obj);
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
	public <T> void removeSetItem(final String key, final T obj) {
		redisTemplate.execute(new RedisCallback<Object>(){
			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] k = defaultSerializer.serialize(key);
				byte[] v = defaultSerializer.serialize(obj);
				if(!connection.sIsMember(k, v)){
					connection.sRem(k, v);
				}
				return null;
			}
		});
	}

	public <T> Set<T> readSet(final String key) {
		return redisTemplate.execute(new RedisCallback<Set<T>>() {
			@Override
			public Set<T> doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] k = defaultSerializer.serialize(key);
				if (connection.exists(k)) {
					Set<byte[]> value = connection.sMembers(k);
					Set<T> rst = new HashSet<T>();
					for(byte[] b:value){
						rst.add((T) defaultSerializer.deserialize(b));
					}
					return rst;
				}
				return null;
			}
		});
	}

	@Override
	public <T> T read(String key, Class<T> type) {
		return this.read(key);
	}

	@Override
	public <T> void delete(String key, Class<T> type) {
		this.delete(key);
	}

	@Override
	public <T> Set<T> readSet(String key, Class<T> type) {
		return this.readSet(key);
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
				byte[] k = defaultSerializer.serialize(key);
				byte[] v = defaultSerializer.serialize(obj);
					connection.lPush(k, v);
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
				byte[] keys = defaultSerializer.serialize(key);
				if (connection.exists(keys)) {
					List<byte[]> value = connection.bRPop(timeout, keys);
                    List<T> values = new ArrayList<T>(value.size());
                    for (byte[] bs : value) {
                        values.add((T) defaultSerializer.deserialize(bs));
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
				byte[] k = defaultSerializer.serialize(key);
				byte[] v = defaultSerializer.serialize(obj);
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
				byte[] k = defaultSerializer.serialize(key);
				if (connection.exists(k)) {
					List<byte[]> value = connection.lRange(k, 0, -1);
                    List<T> values = new ArrayList<T>(value.size());
                    for (byte[] bs : value) {
                        values.add((T) defaultSerializer.deserialize(bs));
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
			@Override
			public Long doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] k = defaultSerializer.serialize(key);
				return connection.incr(k);
			}
		});
	}

	@Override
	public Long incrBy(final String key, final Long integer) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] k = defaultSerializer.serialize(key);
				return connection.incrBy(k, integer);
			}
		});
	}

	@Override
	public RedisSerializer<?> getRedisSerializer() {
		return this.defaultSerializer;
	}
}
