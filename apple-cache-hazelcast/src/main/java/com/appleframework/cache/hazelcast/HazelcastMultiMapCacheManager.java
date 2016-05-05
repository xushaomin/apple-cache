package com.appleframework.cache.hazelcast;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;

/**
 * @author cruise.xu
 * 
 */
@SuppressWarnings("unchecked")
public class HazelcastMultiMapCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(HazelcastMapCacheManager.class);
	
	private static String CACHE_KEY = "spring-cache";

	@Resource
	private HazelcastInstance hazelcastInstance;

	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}
	
	public MultiMap<String, Object> getMap() {
		return hazelcastInstance.getMultiMap(CACHE_KEY);
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

	@Override
	public List<Object> get(List<String> keyList) throws CacheException {
		try {
			List<Object> list = new ArrayList<Object>();
			MultiMap<String, Object> map = this.getMap();
			 for (String key : keyList) {
				 list.add(map.get(key));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public List<Object> get(String... keys) throws CacheException {
		try {
			List<Object> list = new ArrayList<Object>();
			MultiMap<String, Object> map = this.getMap();
			for (String key : keys) {
				 list.add(map.get(key));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> get(Class<T> clazz, List<String> keyList) throws CacheException {
		try {
			List<T> list = new ArrayList<T>();
			MultiMap<String, Object> map = this.getMap();
			for (String key : keyList) {
				 list.add((T)map.get(key));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> get(Class<T> clazz, String... keys) throws CacheException {
		try {
			List<T> list = new ArrayList<T>();
			MultiMap<String, Object> map = this.getMap();
			for (String key : keys) {
				 list.add((T)map.get(key));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

}
