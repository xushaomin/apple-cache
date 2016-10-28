package com.appleframework.cache.jedis.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;

import redis.clients.jedis.JedisPool;

public class SpringCache implements Cache {

	private String name;
	private CacheOperation cacheOperation;
	
	public SpringCache(JedisPool jedisPool, String name) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(name, jedisPool);
	}
	
	public SpringCache(JedisPool jedisPool, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(name, expire, jedisPool);
	}
	
	@Override
	public void clear() {
		if(CacheConfig.isCacheEnable())
			cacheOperation.clear();
	}

	@Override
	public void evict(Object key) {
		if(CacheConfig.isCacheEnable())
			cacheOperation.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		if(!CacheConfig.isCacheEnable())
			return wrapper;
		Object value = cacheOperation.get(key.toString());
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
	public CacheOperation getNativeCache() {
		return this.cacheOperation;
	}

	@Override
	public void put(Object key, Object value) {
		if(CacheConfig.isCacheEnable())
			cacheOperation.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if(!CacheConfig.isCacheEnable())
			return null;
		Object cacheValue = this.cacheOperation.get(key.toString());
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
		if(!CacheConfig.isCacheEnable())
			return wrapper;
		Object objValue = this.cacheOperation.get(key.toString());
		if (objValue != null) {
			wrapper = new SimpleValueWrapper(objValue);
		}
		else {
			wrapper = new SimpleValueWrapper(value);
			this.cacheOperation.put(key.toString(), value);
		}
		return wrapper;
	}

}