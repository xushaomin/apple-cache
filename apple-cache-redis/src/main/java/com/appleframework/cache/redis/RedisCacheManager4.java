package com.appleframework.cache.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

public class RedisCacheManager4 implements CacheManager {

	private static Logger logger = Logger.getLogger(RedisCacheManager4.class);
	
	private String name = "REDIS_CACHE_MANAGERS";
	
	private List<RedissonClient> redRedissonList;
	
	private List<RedissonClient> writeRedissonList;
	
	public void setRedRedissonList(List<RedissonClient> redRedissonList) {
		this.redRedissonList = redRedissonList;
	}

	public void setWriteRedissonList(List<RedissonClient> writeRedissonList) {
		this.writeRedissonList = writeRedissonList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void clear() throws CacheException {
		for (RedissonClient redisson : writeRedissonList) {
			try {
				redisson.getMapCache(name).clear();
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}
	
	private RedissonClient getRandomReadRedisson() {
		if(redRedissonList.size() > 1) {
			int i = (int) Math.round(Math.random() * 1000.0D) % this.redRedissonList.size();
			return this.redRedissonList.get(i);
		}
		else {
			return redRedissonList.get(0);
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return this.getRandomReadRedisson().getMapCache(name).get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return (T)this.getRandomReadRedisson().getMapCache(name).get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		for (RedissonClient redisson : writeRedissonList) {
			try {
				redisson.getMapCache(name).remove(key);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
		return true;
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			for (RedissonClient redisson : writeRedissonList) {
				try {
					redisson.getMapCache(name).put(key, value);
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new CacheException(e.getMessage());
				}
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			for (RedissonClient redisson : writeRedissonList) {
				try {
					redisson.getMapCache(name).put(key, value, expireTime, TimeUnit.SECONDS);
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new CacheException(e.getMessage());
				}
			}
		}
	}

}