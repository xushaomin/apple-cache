package com.appleframework.cache.memcache.spring;

import net.rubyeye.xmemcached.MemcachedClient;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.appleframework.cache.core.spring.CacheOperation;

public class SpringCache implements Cache {

	private String name;
	private CacheOperation cacheOperation;
	private boolean isOpen = true;
	
	public SpringCache(String name, MemcachedClient memcachedClient) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(memcachedClient, name);
	}
	
	public SpringCache(MemcachedClient memcachedClient, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(memcachedClient, name, expire);
	}
	
	public SpringCache(MemcachedClient memcachedClient, String name, int expire, boolean isOpen) {
		this.name = name;
		this.isOpen = isOpen;
		this.cacheOperation = new SpringCacheOperation(memcachedClient, name, expire);
	}

	@Override
	public void clear() {
		if(isOpen)
			cacheOperation.clear();
	}

	@Override
	public void evict(Object key) {
		if(isOpen)
			cacheOperation.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		if(!isOpen)
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
		if(isOpen)
			cacheOperation.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
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
		if(!isOpen)
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