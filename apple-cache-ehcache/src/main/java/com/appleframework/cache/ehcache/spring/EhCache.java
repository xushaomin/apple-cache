package com.appleframework.cache.ehcache.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import net.sf.ehcache.CacheManager;

public class EhCache implements Cache {

	private final String name;
	private final EhCacheOp ehCacheOp;

	public EhCache(String name, int expire, CacheManager cacheManager) {
		this.name = name;
		this.ehCacheOp = new EhCacheOp(name, expire, cacheManager);
	}
	
	public EhCache(String name, CacheManager cacheManager) {
		this.name = name;
		this.ehCacheOp = new EhCacheOp(name, cacheManager);
	}

	@Override
	public void clear() {
		ehCacheOp.clear();
	}

	@Override
	public void evict(Object key) {
		ehCacheOp.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		Object value = ehCacheOp.get(key.toString());
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
	public EhCacheOp getNativeCache() {
		return this.ehCacheOp;
	}

	@Override
	public void put(Object key, Object value) {
		ehCacheOp.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object cacheValue = this.ehCacheOp.get(key.toString());
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
		Object objValue = this.ehCacheOp.get(key.toString());
		if (objValue != null) {
			wrapper = new SimpleValueWrapper(objValue);
		}
		else {
			wrapper = new SimpleValueWrapper(value);
			this.ehCacheOp.put(key.toString(), value);
		}
		return wrapper;
	}

}