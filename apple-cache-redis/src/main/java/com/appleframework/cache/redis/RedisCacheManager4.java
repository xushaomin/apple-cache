package com.appleframework.cache.redis;

import java.util.List;

import org.apache.log4j.Logger;
import org.redisson.Redisson;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

public class RedisCacheManager4 implements CacheManager {

	private static Logger logger = Logger.getLogger(RedisCacheManager4.class);
	
	private String name = "REDIS_CACHE_MANAGERS";
	
	private List<Redisson> redRedissonList;
	
	private List<Redisson> writeRedissonList;
	
	public void setRedRedissonList(List<Redisson> redRedissonList) {
		this.redRedissonList = redRedissonList;
	}

	public void setWriteRedissonList(List<Redisson> writeRedissonList) {
		this.writeRedissonList = writeRedissonList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void clear() throws CacheException {
		for (Redisson redisson : writeRedissonList) {
			try {
				redisson.getMap(name).clear();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public Object get(String key) throws CacheException {
		for (Redisson redisson : redRedissonList) {
			try {
				return redisson.getMap(name).get(key);
			} catch (Exception e) {
				logger.error(e.getMessage());
				continue;
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		for (Redisson redisson : redRedissonList) {
			try {
				return (T)redisson.getMap(name).get(key);
			} catch (Exception e) {
				logger.error(e.getMessage());
				continue;
			}
		}
		return null;
	}

	public boolean remove(String key) throws CacheException {
		for (Redisson redisson : writeRedissonList) {
			try {
				redisson.getMap(name).remove(key);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return true;
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			for (Redisson redisson : writeRedissonList) {
				try {
					redisson.getMap(name).put(key, value);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		this.set(key, obj);
	}

}