package com.appleframework.cache.redis.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import redis.clients.jedis.JedisPool;

public class JedisCache implements Cache {

	private final String name;
	private final RedisCache redisCache;

	public JedisCache(String name, int expire, JedisPool jedisPool) {
		this.name = name;
		this.redisCache = new RedisCache(name, expire, jedisPool);
	}
	
	public JedisCache(String name, JedisPool jedisPool) {
		this.name = name;
		this.redisCache = new RedisCache(name, jedisPool);
	}

	@Override
	public void clear() {
		redisCache.clear();
	}

	@Override
	public void evict(Object key) {
		redisCache.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		Object value = redisCache.get(key.toString());
		if (value != null) {
			wrapper = new SimpleValueWrapper(value);
		}
		return wrapper;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public RedisCache getNativeCache() {
		return this.redisCache;
	}

	@Override
	public void put(Object key, Object value) {
		redisCache.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object cacheValue = this.redisCache.get(key.toString());
		Object value = (cacheValue != null ? cacheValue : null);
		if (type != null && !type.isInstance(value)) {
			throw new IllegalStateException(
					"Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		ValueWrapper wrapper = null;
		Object objValue = this.redisCache.get(key.toString());
		if (objValue != null) {
			wrapper = new SimpleValueWrapper(objValue);
		}
		else {
			wrapper = new SimpleValueWrapper(value);
			this.redisCache.put(key.toString(), value);
		}
		return wrapper;
	}

}