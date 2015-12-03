package com.appleframework.cache.hazelcast;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.hazelcast.core.HazelcastInstance;

/**
 * @author cruise.xu
 * 
 */
public class HazelcastCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(HazelcastCacheManager.class);
	
	private static String CACHE_KEY = "spring-cache";

	@Resource
	private HazelcastInstance hazelcastInstance;

	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}
	
	public Map<String, Object> getMap() {
		return hazelcastInstance.getMap(CACHE_KEY);
	}

	public void clear() throws CacheException {
		try {
			getMap().clear();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return getMap().get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return (T)getMap().get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			getMap().remove(key);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public void set(String key, Object obj) throws CacheException {
		if (null != obj) {
			try {
				getMap().put(key, obj);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		this.set(key, obj);
	}

}
