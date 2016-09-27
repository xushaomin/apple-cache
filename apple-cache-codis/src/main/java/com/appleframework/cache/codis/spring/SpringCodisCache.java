package com.appleframework.cache.codis.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.appleframework.cache.codis.CodisResourcePool;

public class SpringCodisCache implements Cache {

	private final String name;
	private final CodisCacheOperation codisCache;

	public SpringCodisCache(String name, int expire, CodisResourcePool codisResourcePool) {
		this.name = name;
		this.codisCache = new CodisCacheOperation(name, expire, codisResourcePool);
	}
	
	public SpringCodisCache(String name, CodisResourcePool codisResourcePool) {
		this.name = name;
		this.codisCache = new CodisCacheOperation(name, codisResourcePool);
	}

	@Override
	public void clear() {
		codisCache.clear();
	}

	@Override
	public void evict(Object key) {
		codisCache.delete(key.toString());
	}

	@Override
	public ValueWrapper get(Object key) {
		ValueWrapper wrapper = null;
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
		codisCache.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
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