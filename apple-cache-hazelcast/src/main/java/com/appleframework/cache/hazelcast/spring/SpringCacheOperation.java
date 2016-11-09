package com.appleframework.cache.hazelcast.spring;

import java.util.Map;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;
import com.hazelcast.core.HazelcastInstance;

public class SpringCacheOperation implements CacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperation.class);

	private String name;
	private int expire = 0;
	
	private final HazelcastInstance instance;
	
	public Map<String, Object> getCacheMap() {
		return instance.getMap(name);
	}

	public SpringCacheOperation(HazelcastInstance instance, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.instance = instance;
	}

	public Object get(String key) {
		Object returnValue = null;
		try {
			Object cacheValue = getCacheMap().get(key);
			if(CacheConfig.isCacheObject()) {
				CacheObject cache = (CacheObject) cacheValue;
				if (null != cache) {
					if (cache.isExpired()) {
						this.resetCacheObject(key, cache);
					} else {
						returnValue = cache.getObject();
					}
				}
			}
			else {
				return cacheValue;
			}
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
		return returnValue;
	}

	private void resetCacheObject(String key, CacheObject cache) {
		try {
			cache.setExpiredTime(getExpiredTime());
			getCacheMap().put(key, cache);
		} catch (Exception e) {
			logger.warn("cache error", e);
		} 
	}
	
	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			if(CacheConfig.isCacheObject()) {
				CacheObject cache = new CacheObjectImpl(value, getExpiredTime());
				getCacheMap().put(key, cache);
			}
			else {
				if(expire > 0)
					getCacheMap().put(key, value);
				else
					getCacheMap().put(key, value);
			}
			
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void clear() {
		try {
			getCacheMap().clear();
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void delete(String key) {
		try {
			getCacheMap().remove(key);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public int getExpire() {
		return expire;
	}
	
	private long getExpiredTime() {
		long lastTime = 2592000000L;
		if (expire > 0) {
			lastTime = expire * 1000;
		}
		return System.currentTimeMillis() + lastTime;
	}
	
}
