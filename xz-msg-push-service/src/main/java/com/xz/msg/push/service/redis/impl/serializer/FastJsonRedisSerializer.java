package com.xz.msg.push.service.redis.impl.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import java.lang.reflect.Type;

/**
 * 
 * @author Yan
 *
 * @param <T>
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

	private final Type javaType;

	public FastJsonRedisSerializer(Type javaType) {
		super();
		this.javaType = javaType;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		return JSON.toJSONBytes(t, SerializerFeature.WriteNullListAsEmpty);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if((bytes == null || bytes.length == 0)){
			return null;
		}
		return JSON.parseObject(bytes, javaType, Feature.IgnoreNotMatch);
	}

}
