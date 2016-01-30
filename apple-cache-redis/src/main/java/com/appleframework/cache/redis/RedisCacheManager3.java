package com.appleframework.cache.redis;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RMapCache;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

public class RedisCacheManager3 implements CacheManager {

	private static Logger logger = Logger.getLogger(RedisCacheManager3.class);
	
	private String name = "REDIS_CACHE_MANAGER";
	
	private RedissonClient redisson;

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public RMapCache<String, Object> getCacheMap() {
		return redisson.getMapCache(name);
	}

	public void clear() throws CacheException {
		try {
			getCacheMap().clear();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return getCacheMap().get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return (T)getCacheMap().get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			getCacheMap().remove(key);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getCacheMap().put(key, value);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				getCacheMap().put(key, value, expireTime, TimeUnit.SECONDS);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
}