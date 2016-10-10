package com.appleframework.cache.memcache.spring;

import net.rubyeye.xmemcached.MemcachedClient;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

public class SpringMemcachedCache implements Cache {

	private String name;
	private MemcachedCacheOperation operation;
	private boolean isOpen = true;
	
	public SpringMemcachedCache(String name, MemcachedClient memcachedClient) {
		this.name = name;
		this.operation = new MemcachedCacheOperation(memcachedClient, name);
	}
	
	public SpringMemcachedCache(MemcachedClient memcachedClient, String name, int expire) {
		this.name = name;
		this.operation = new MemcachedCacheOperation(memcachedClient, name, expire);
	}
	
	public SpringMemcachedCache(MemcachedClient memcachedClient, String name, int expire, boolean isOpen) {
		this.name = name;
		this.isOpen = isOpen;
		this.operation = new MemcachedCacheOperation(memcachedClient, name, expire);
	}

	@Override
	public void clear() {
		if(isOpen)
			operation.clear();
	}

	@Override
	public void evict(Object key) {
		if(isOpen)
			operation.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		if(!isOpen)
			return wrapper;
		Object value = operation.get(key.toString());
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
	public MemcachedCacheOperation getNativeCache() {
		return this.operation;
	}

	@Override
	public void put(Object key, Object value) {
		if(isOpen)
			operation.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object cacheValue = this.operation.get(key.toString());
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
		Object objValue = this.operation.get(key.toString());
		if (objValue != null) {
			wrapper = new SimpleValueWrapper(objValue);
		}
		else {
			wrapper = new SimpleValueWrapper(value);
			this.operation.put(key.toString(), value);
		}
		return wrapper;
	}
	
}