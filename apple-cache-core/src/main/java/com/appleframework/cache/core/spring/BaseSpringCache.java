package com.appleframework.cache.core.spring;

import java.util.concurrent.Callable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.appleframework.cache.core.config.SpringCacheConfig;

public abstract class BaseSpringCache implements Cache {
	
	protected BaseCacheOperation cacheOperation;

	protected String name;

	// 删除缓存中的所有数据。需要注意的是，具体实现中只删除使用@Cacheable注解缓存的所有数据，不要影响应用内的其他缓存
	@Override
	public void clear() {
		if(SpringCacheConfig.isCacheEnable()) {
			cacheOperation.clear();
		}
	}

	// 删除缓存
	@Override
	public void evict(Object key) {
		if(SpringCacheConfig.isCacheEnable()) {
			cacheOperation.delete(key.toString());
		}
	}

	// 通过key获取缓存值，注意返回的是ValueWrapper，为了兼容存储空值的情况，将返回值包装了一层，通过get方法获取实际值
	@Override
	public ValueWrapper get(Object key) {
		if(!SpringCacheConfig.isCacheEnable()) {
			return null;
		}
		ValueWrapper wrapper = null;
		Object value = cacheOperation.get(key.toString());
		if (value != null) {
			wrapper = new SimpleValueWrapper(value);
		}
		return wrapper;
	}

	// cacheName，缓存的名字，默认实现中一般是CacheManager创建Cache的bean时传入cacheName
	@Override
	public String getName() {
		return this.name;
	}

	// 获取实际使用的缓存，如：RedisTemplate、com.github.benmanes.caffeine.cache.Cache<Object, Object>。
	// 暂时没发现实际用处，可能只是提供获取原生缓存的bean，以便需要扩展一些缓存操作或统计之类的东西
	@Override
	public BaseCacheOperation getNativeCache() {
		return this.cacheOperation;
	}

	// 将@Cacheable注解方法返回的数据放入缓存中
	@Override
	public void put(Object key, Object value) {
		if(SpringCacheConfig.isCacheEnable()) {
			cacheOperation.put(key.toString(), value);
		}
	}
	
	// 通过key获取缓存值，可以使用valueLoader.call()来调使用@Cacheable注解的方法。
	// 当@Cacheable注解的sync属性配置为true时使用此方法。因此方法内需要保证回源到数据库的同步性。
	// 避免在缓存失效时大量请求回源到数据库
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Callable<T> valueLoader) {
		if(!SpringCacheConfig.isCacheEnable()) {
			return null;
		}
		Object cacheValue = this.cacheOperation.get(key.toString());
		Object value = (cacheValue != null ? cacheValue : null);
		return (T) value;
	}

	// 通过key获取缓存值，返回的是实际值，即方法的返回值类型
	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if(!SpringCacheConfig.isCacheEnable()) {
			return null;
		}
		Object cacheValue = this.cacheOperation.get(key.toString());
		Object value = (cacheValue != null ? cacheValue : null);
		if (type != null && !type.isInstance(value)) {
			throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	// 当缓存中不存在key时才放入缓存。返回值是当key存在时原有的数据
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		if(!SpringCacheConfig.isCacheEnable()) {
			return null;
		}
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