package com.appleframework.cache.j2cache.spring;

import org.redisson.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import net.sf.ehcache.CacheManager;

public class SpringJ2Cache implements Cache {

	private String name;
	private boolean isOpen = true;
	private J2CacheOperation j2CacheOp;
	
	public SpringJ2Cache(CacheManager cacheManager, RedissonClient redisson, String name) {
		this.name = name;
		this.j2CacheOp = new J2CacheOperation(cacheManager, redisson, name);
	}

	public SpringJ2Cache(CacheManager cacheManager, RedissonClient redisson, String name, int expire) {
		this.name = name;
		this.j2CacheOp = new J2CacheOperation(cacheManager, redisson, name, expire);
	}
	
	public SpringJ2Cache(CacheManager cacheManager, RedissonClient redisson, String name, int expire, boolean isOpen) {
		this.name = name;
		this.isOpen = isOpen;
		this.j2CacheOp = new J2CacheOperation(cacheManager, redisson, name, expire, isOpen);
	}

	@Override
	public void clear() {
		if(isOpen)
			j2CacheOp.clear();
	}

	@Override
	public void evict(Object key) {
		if(isOpen)
			j2CacheOp.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		if(!isOpen)
			return wrapper;
		Object value = j2CacheOp.get(key.toString());
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
	public J2CacheOperation getNativeCache() {
		return this.j2CacheOp;
	}

	@Override
	public void put(Object key, Object value) {
		if(isOpen)
			j2CacheOp.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if(!isOpen)
			return null;
		Object cacheValue = this.j2CacheOp.get(key.toString());
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
		Object objValue = this.j2CacheOp.get(key.toString());
		if (objValue != null) {
			wrapper = new SimpleValueWrapper(objValue);
		}
		else {
			wrapper = new SimpleValueWrapper(value);
			this.j2CacheOp.put(key.toString(), value);
		}
		return wrapper;
	}

}