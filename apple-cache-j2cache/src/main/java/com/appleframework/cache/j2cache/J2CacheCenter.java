package com.appleframework.cache.j2cache;

import java.util.Map;

import org.apache.log4j.Logger;
import org.redisson.Redisson;

import com.appleframework.cache.core.CacheCenter;
import com.appleframework.cache.core.CacheException;

public class J2CacheCenter implements CacheCenter {

	private static Logger logger = Logger.getLogger(J2CacheCenter.class);
		
	private Redisson redisson;

	public void setRedisson(Redisson redisson) {
		this.redisson = redisson;
	}

	public Map<String, Object> getRedisCache(String name) {
		return redisson.getMap(name);
	}
	

	public void clear(String name) throws CacheException {
		try {
			getRedisCache(name).clear();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String name, String key) throws CacheException {
		try {
			return getRedisCache(name).get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked" })
	public <T> T get(String name, String key, Class<T> clazz) throws CacheException {
		try {
			return (T)getRedisCache(name).get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String name, String key) throws CacheException {
		try {
			getRedisCache(name).remove(key);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public void set(String name, String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getRedisCache(name).put(key, value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String name, String key, Object obj, int expireTime) throws CacheException {
		this.set(name, key, obj);
	}
	
}