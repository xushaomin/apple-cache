package com.appleframework.cache.redis;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.redisson.core.RKeys;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.CacheService;

public class RedissonBucketCacheService implements CacheService {

	private static Logger logger = Logger.getLogger(RedissonBucketCacheService.class);
		
	private RedissonClient redisson;

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
	
	public RBucket<Object> getRedissonCache(String name) {
		return redisson.getBucket(name);
	}

	@Override
	public CacheObject getCache(String key) {
		return (CacheObject)getRedissonCache(key).get();
	}

	public void clear() throws CacheException {
		try {
			RKeys keys = redisson.getKeys();
			for (String key : keys.getKeys()) {
				getRedissonCache(key).delete();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			CacheObject cache = (CacheObject)getRedissonCache(key).get();
			if(cache.isExpired()) {
				throw new CacheException("is expired");
			}
			return cache.getObject();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public void put(String key, Object value) {
		if (null != value) {
			try {
				CacheObject cache = new CacheObjectImpl(value, -1);
				getRedissonCache(key).set(cache);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}

	@Override
	public void put(String key, Object value, int expired) {
		long expiredL = System.currentTimeMillis() + expired * 1000;
		if (null != value) {
			try {
				CacheObject cache = new CacheObjectImpl(value, expiredL);
				getRedissonCache(key).set(cache);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}

	@Override
	public Object remove(String key) {
		try {
			Object object = null;
			CacheObject cache = (CacheObject)getRedissonCache(key).get();
			if(null != cache) {
				getRedissonCache(key).delete();
			}
			else {
				throw new CacheException("is not exist");
			}
			if(cache.isExpired()) {
				throw new CacheException("is expired");
			}
			else {
				object = cache.getObject();
			}
			return object;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public CacheObject removeCache(String key) {
		try {
			CacheObject cache = (CacheObject)getRedissonCache(key).get();
			if(null != cache) {
				getRedissonCache(key).delete();
			}
			else {
				throw new CacheException("is not exist");
			}
			return cache;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}
	
}