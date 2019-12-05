package com.appleframework.cache.redis.spring;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseCacheOperation;

public class SpringCacheOperationHset implements BaseCacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperationHset.class);

	private String name;
	private int expireTime = 0;
	private RedisTemplate<String, Object> redisTemplate;

	public SpringCacheOperationHset(String name, int expireTime, RedisTemplate<String, Object> redisTemplate) {
		this.name = name;
		this.expireTime = expireTime;
		this.redisTemplate = redisTemplate;
	}

	private String getNameKey() {
		return SpringCacheConfig.getCacheKeyPrefix() + name;
	}

	public Object get(String key) {
		Object object = null;
		try {
			Object cacheValue = redisTemplate.opsForHash().get(getNameKey(), key);
			if (null != cacheValue) {
				CacheObject cache = (CacheObject) cacheValue;
				if (null != cache) {
					if (cache.isExpired()) {
						this.resetCacheObject(key, cache);
					} else {
						object = cache.getObject();
					}
				}
			}
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
		return object;
	}

	private void resetCacheObject(String key, CacheObject cache) {
		try {
			cache.setExpiredTime(getExpiredTime());
			redisTemplate.opsForHash().put(getNameKey(), key, cache);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		try {
			Object cache = new CacheObjectImpl(value, getExpiredTime());
			String nameKey = getNameKey();
			redisTemplate.opsForHash().put(nameKey, key, cache);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void clear() {
		try {
			redisTemplate.delete(getNameKey());
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void delete(String key) {
		try {
			redisTemplate.opsForHash().delete(getNameKey(), key);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public int getExpireTime() {
		return expireTime;
	}

	private long getExpiredTime() {
		long lastTime = 2592000000L;
		if (expireTime > 0) {
			lastTime = expireTime * 1000;
		}
		return System.currentTimeMillis() + lastTime;
	}
}
