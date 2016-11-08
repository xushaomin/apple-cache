package com.appleframework.cache.codis.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;

public class SpringCache implements Cache {
	
	private CacheOperation cacheOperation;

	private String name;
	
	public SpringCache(CodisResourcePool codisResourcePool, String name) {
		this.name = name;
		if(CacheConfig.isCacheObject()) {
			this.cacheOperation = new SpringCacheOperationHset(codisResourcePool, name);
		}
		else {
			this.cacheOperation = new SpringCacheOperationBucket(codisResourcePool, name);
		}
	}
	
	public SpringCache(CodisResourcePool codisResourcePool, String name, int expire) {
		this.name = name;
		if(CacheConfig.isCacheObject()) {
			this.cacheOperation = new SpringCacheOperationHset(codisResourcePool, name);
		}
		else {
			this.cacheOperation = new SpringCacheOperationBucket(codisResourcePool, name);
		}
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
		if(!CacheConfig.isCacheEnable())
			return null;
		ValueWrapper wrapper = null;
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
		if(!CacheConfig.isCacheEnable())
			return null;
		ValueWrapper wrapper = null;
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