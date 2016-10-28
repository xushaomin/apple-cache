package com.appleframework.cache.redisson.spring;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RMapCache;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;

public class SpringCacheOperation implements CacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperation.class);

	private String name;
	private int expire = 0;
	
	private final RedissonClient redisson;
	
	public RMapCache<String, Object> getCacheMap() {
		return redisson.getMapCache(name);
	}

	public SpringCacheOperation(RedissonClient redisson, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.redisson = redisson;
	}
	
	public SpringCacheOperation(RedissonClient redisson, String name) {
		this.name = name;
		this.expire = 0;
		this.redisson = redisson;
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
			this.delete(key);
		try {
			if(CacheConfig.isCacheObject()) {
				CacheObject cache = new CacheObjectImpl(value, getExpiredTime());
				getCacheMap().put(key, cache);
			}
			else {
				if(expire > 0)
					getCacheMap().put(key, value, expire, TimeUnit.SECONDS);
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
