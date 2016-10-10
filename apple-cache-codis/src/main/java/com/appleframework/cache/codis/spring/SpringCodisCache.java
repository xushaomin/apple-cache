package com.appleframework.cache.codis.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.appleframework.cache.codis.CodisResourcePool;

public class SpringCodisCache implements Cache {
	
	private CodisCacheOperation codisCache;

	private String name;
	private boolean isOpen = true;
	
	public SpringCodisCache(CodisResourcePool codisResourcePool, String name) {
		this.name = name;
		this.codisCache = new CodisCacheOperation(codisResourcePool, name);
	}
	
	public SpringCodisCache(CodisResourcePool codisResourcePool, String name, int expire) {
		this.name = name;
		this.codisCache = new CodisCacheOperation(codisResourcePool, name, expire);
	}
	
	public SpringCodisCache(CodisResourcePool codisResourcePool, String name, int expire, boolean isOpen) {
		this.name = name;
		this.isOpen = isOpen;
		this.codisCache = new CodisCacheOperation(codisResourcePool, name, expire);
	}

	@Override
	public void clear() {
		if(isOpen)
			codisCache.clear();
	}

	@Override
	public void evict(Object key) {
		if(isOpen)
			codisCache.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
		if(!isOpen)
			return wrapper;
		Object value = codisCache.get(key.toString());
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
	public CodisCacheOperation getNativeCache() {
		return this.codisCache;
	}

	@Override
	public void put(Object key, Object value) {
		if(isOpen)
			codisCache.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if(!isOpen)
			return null;
		Object cacheValue = this.codisCache.get(key.toString());
		Object value = (cacheValue != null ? cacheValue : null);
		if (type != null && !type.isInstance(value)) {
			throw new IllegalStateException(
					"Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		if(!isOpen)
			return null;
		ValueWrapper wrapper = null;
		Object objValue = this.codisCache.get(key.toString());
		if (objValue != null) {
			wrapper = new SimpleValueWrapper(objValue);
		}
		else {
			wrapper = new SimpleValueWrapper(value);
			this.codisCache.put(key.toString(), value);
		}
		return wrapper;
	}

}