package com.appleframework.cache.ehcache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@SuppressWarnings("unchecked")
public class EhCacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = Logger.getLogger(EhCacheManager.class);
	
	private String name = "EHCACHE_MANAGER";
	
	private CacheManager ehcacheManager;
		
	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}
	
	public Cache getEhCache() {
		Cache cache = ehcacheManager.getCache(name);
		if(null == cache) {
			ehcacheManager.addCache(name);
			return ehcacheManager.getCache(name);
		}
		else {
			return cache;
		}
	}

	public void clear() throws CacheException {
		try {
			getEhCache().removeAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			Object value = null;
			Element element = getEhCache().get(key);
			if(null != element) {
				value = element.getObjectValue();
			}
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
		
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			T value = null;
			Element element = getEhCache().get(key);
			if(null != element) {
				value = (T)element.getObjectValue();
			}			
			return value;
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
				Element element = new Element(key, value);
				getEhCache().put(element);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				Element element = new Element(key, value, expireTime, expireTime);
				getEhCache().put(element);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		try {
			List<Object> list = new ArrayList<Object>();
			Map<Object, Element> map = this.getEhCache().getAll(keyList);
			for (String key : keyList) {
				Element element = map.get(key);
				if(null != element) {
					list.add(element.getObjectValue());
				}
				else {
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
			Map<Object, Element> map = this.getEhCache().getAll(keyList);
			for (String key : keyList) {
				Element element = map.get(key);
				if(null != element) {
					list.add((T)element.getObjectValue());
				}
				else {
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
		try {
			List<T> list = new ArrayList<T>();
			Cache cache = this.getEhCache();
			for (String key : keys) {
				Element element = cache.get(key);
				if(null != element) {
					list.add((T)element.getObjectValue());
				}
				else {
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
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<Object, Element> cacheMap = this.getEhCache().getAll(keyList);
			for (String key : keyList) {
				Element element = cacheMap.get(key);
				if(null != element) {
					map.put(key, element.getObjectValue());
				}
				else {
					map.put(key, null);
				}
			}
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
			Map<String, T> map = new HashMap<String, T>();
			Map<Object, Element> cacheMap = this.getEhCache().getAll(keyList);
			for (String key : keyList) {
				Element element = cacheMap.get(key);
				if(null != element) {
					map.put(key, (T)element.getObjectValue());
				}
				else {
					map.put(key, null);
				}
			}
			return map;
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