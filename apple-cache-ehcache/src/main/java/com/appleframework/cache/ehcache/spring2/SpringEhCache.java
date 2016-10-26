package com.appleframework.cache.ehcache.spring2;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import net.sf.ehcache.CacheManager;

public class SpringEhCache implements Cache {

	private String name;
	private boolean isOpen = true;
	private EhcacheOperation ehCacheOp;

	public SpringEhCache(CacheManager cacheManager, String name) {
		this.name = name;
		this.ehCacheOp = new EhcacheOperation(cacheManager, name);
	}
	
	public SpringEhCache(CacheManager cacheManager, String name, int expire) {
		this.name = name;
		this.ehCacheOp = new EhcacheOperation(cacheManager, name, expire);
	}
	
	public SpringEhCache(CacheManager cacheManager, String name, int expire, boolean isOpen) {
		this.name = name;
		this.isOpen = isOpen;
		this.ehCacheOp = new EhcacheOperation(cacheManager, name, expire);
	}

	@Override
	public void clear() {
		if(isOpen)
			ehCacheOp.clear();
	}

	@Override
	public void evict(Object key) {
		if(isOpen)
			ehCacheOp.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		if(!isOpen)
			return wrapper;
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
	public EhcacheOperation getNativeCache() {
		return this.ehCacheOp;
	}

	@Override
	public void put(Object key, Object value) {
		if(isOpen)
			ehCacheOp.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if(!isOpen)
			return null;
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
		if(!isOpen)
			return wrapper;
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