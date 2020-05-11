package com.appleframework.cache.ehcache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.ehcache.config.EhCacheConfiguration;
import com.appleframework.cache.ehcache.config.EhCacheProperties;
import com.appleframework.cache.ehcache.utils.EhCacheConfigurationUtil;
import com.appleframework.cache.ehcache.utils.EhCacheExpiryUtil;

@SuppressWarnings("unchecked")
public class EhCacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = LoggerFactory.getLogger(EhCacheManager.class);

    private volatile Cache<String, Serializable> cache;  

	private String name = "default";

	private CacheManager ehcacheManager;

	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public Cache<String, Serializable> getEhCache() {
		if (cache == null) {
			synchronized (Cache.class) {
				if (cache == null) {
					initCache();
				}
			}
		}
		return cache;
	}
	
	private void initCache() {
		EhCacheProperties properties = null;
		Map<String, EhCacheProperties> cacheTemplate = EhCacheConfiguration.getProperties();
		if(null != cacheTemplate.get(name) ) {
			properties = cacheTemplate.get(name);
		}
		
		cache = ehcacheManager.getCache(name, String.class, Serializable.class);
		if (null == cache) {
			try {
				CacheConfigurationBuilder<String, Serializable> configuration 
					= EhCacheConfigurationUtil.initCacheConfiguration(properties);
				cache = ehcacheManager.createCache(name, configuration);
			} catch (IllegalArgumentException e) {
				logger.warn("the cache name " + name + " is exist !");
				cache = ehcacheManager.getCache(name, String.class, Serializable.class);
			}
		}
	}
	
	public void clear() throws CacheException {
		try {
			getEhCache().clear();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return getEhCache().get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return (T) getEhCache().get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			getEhCache().remove(key);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	public void expire(String key, int expireTime) throws CacheException {
		try {
			EhCacheExpiryUtil.setExpiry(key, expireTime);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getEhCache().put(key, (Serializable) value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				EhCacheExpiryUtil.setExpiry(key, expireTime);
				getEhCache().put(key, (Serializable) value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		try {
			List<Object> list = new ArrayList<Object>();
			Map<String, Object> map = this.getMap(keyList);
			for (String key : keyList) {
				Object value = map.get(key);
				if (null != value) {
					list.add(value);
				} else {
					list.add(null);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		return this.getList(Arrays.asList(keys));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		try {
			List<T> list = new ArrayList<T>();
			Map<String, T> map = this.getMap(clazz, keyList);
			for (String key : keyList) {
				Object value = map.get(key);
				if (null != value) {
					list.add((T) value);
				} else {
					list.add(null);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		return this.getList(clazz, Arrays.asList(keys));
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		try {
			Set<String> keys = new HashSet<String>();
			keys.addAll(keyList);
			Map map = this.getEhCache().getAll(keys);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		return this.getMap(Arrays.asList(keys));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		try {
			Set<String> keys = new HashSet<String>();
			keys.addAll(keyList);
			return (Map<String, T>) this.getEhCache().getAll(keys);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		return this.getMap(clazz, Arrays.asList(keys));
	}

}