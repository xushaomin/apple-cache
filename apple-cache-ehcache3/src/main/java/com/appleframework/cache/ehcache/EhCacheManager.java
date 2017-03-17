package com.appleframework.cache.ehcache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.ehcache.factory.ConfigurationFactoryBean;

@SuppressWarnings("unchecked")
public class EhCacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = Logger.getLogger(EhCacheManager.class);

	private String name = "EHCACHE_MANAGER";

	private CacheManager ehcacheManager;
	
	private EhCacheExpiry expiry;

	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public Cache<String, Serializable> getEhCache() {
		Cache<String, Serializable> cache = ehcacheManager.getCache(name, String.class, Serializable.class);
		if (null == cache) {
			expiry = new EhCacheExpiry();

			CacheConfigurationBuilder<String, Serializable> configuration = CacheConfigurationBuilder
					.newCacheConfigurationBuilder(String.class, Serializable.class,
							ResourcePoolsBuilder.newResourcePoolsBuilder()
									.heap(ConfigurationFactoryBean.getHeap(), MemoryUnit.MB)
									.offheap(ConfigurationFactoryBean.getOffheap(), MemoryUnit.MB))
					.withExpiry(expiry);
			try {
				cache = ehcacheManager.createCache(name, configuration);
			} catch (Exception e) {
				logger.warn("the cache name " + name + " is exist !");
			}
		}
		return cache;
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
				expiry.setExpiry(key, expireTime);
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