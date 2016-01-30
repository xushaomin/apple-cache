package com.appleframework.cache.j2cache.spring;

import org.redisson.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import net.sf.ehcache.CacheManager;

public class J2Cache implements Cache {

	private final String name;
	private final J2CacheOp j2CacheOp;

	public J2Cache(String name, int expire, CacheManager cacheManager, RedissonClient redisson) {
		this.name = name;
		this.j2CacheOp = new J2CacheOp(name, expire, cacheManager, redisson);
	}
	
	public J2Cache(String name, CacheManager cacheManager, RedissonClient redisson) {
		this.name = name;
		this.j2CacheOp = new J2CacheOp(name, cacheManager, redisson);
	}

	@Override
	public void clear() {
		j2CacheOp.clear();
	}

	@Override
	public void evict(Object key) {
		j2CacheOp.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
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
	public J2CacheOp getNativeCache() {
		return this.j2CacheOp;
	}

	@Override
	public void put(Object key, Object value) {
		j2CacheOp.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
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