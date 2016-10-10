package com.appleframework.cache.redis.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import redis.clients.jedis.JedisPool;

public class SpringRedisCache implements Cache {

	private String name;
	private boolean isOpen = true;
	private RedisCacheOperation redisCache;
	
	public SpringRedisCache(JedisPool jedisPool, String name) {
		this.name = name;
		this.redisCache = new RedisCacheOperation(name, jedisPool);
	}
	
	public SpringRedisCache(JedisPool jedisPool, String name, int expire) {
		this.name = name;
		this.redisCache = new RedisCacheOperation(name, expire, jedisPool);
	}
	
	public SpringRedisCache(JedisPool jedisPool, String name, int expire, boolean isOpen) {
		this.name = name;
		this.isOpen = isOpen;
		this.redisCache = new RedisCacheOperation(name, expire, jedisPool);
	}

	@Override
	public void clear() {
		if(isOpen)
			redisCache.clear();
	}

	@Override
	public void evict(Object key) {
		if(isOpen)
			redisCache.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		if(!isOpen)
			return wrapper;
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
	public RedisCacheOperation getNativeCache() {
		return this.redisCache;
	}

	@Override
	public void put(Object key, Object value) {
		if(isOpen)
			redisCache.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if(!isOpen)
			return null;
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
		if(!isOpen)
			return wrapper;
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