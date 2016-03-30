package com.appleframework.cache.redis;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

public class RedisCacheManager2 implements CacheManager {

	private static Logger logger = Logger.getLogger(RedisCacheManager2.class);
		
	private RedissonClient redisson;

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
	
	public RBucket<Object> getCache(String name) {
		return redisson.getBucket(name);
	}

	public void clear() throws CacheException {
		try {
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return getCache(key).get();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return (T)getCache(key).get();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			return getCache(key).delete();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getCache(key).set(value);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				getCache(key).set(value, expireTime, TimeUnit.SECONDS);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
}