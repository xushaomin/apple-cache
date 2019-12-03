package com.appleframework.cache.redis.serialize;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.appleframework.cache.core.utils.SerializeUtility;

public class DefaultObjectSerializer implements RedisSerializer<Object> {

	@Override
	public byte[] serialize(Object o) throws SerializationException {
		return SerializeUtility.serialize(o);
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		return SerializeUtility.unserialize(bytes);
	}
}