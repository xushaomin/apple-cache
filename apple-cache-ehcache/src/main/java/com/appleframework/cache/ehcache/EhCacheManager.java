package com.appleframework.cache.ehcache;

import java.util.ArrayList;
import java.util.List;

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
	public List<Object> get(List<String> keyList) throws CacheException {
		try {
			List<Object> list = new ArrayList<Object>();
			Cache cache = this.getEhCache();
			for (String key : keyList) {
				Element element = cache.get(key);
				if(null != element) {
					list.add(element.getObjectValue());
				}
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
			Cache cache = this.getEhCache();
			for (String key : keys) {
				Element element = cache.get(key);
				if(null != element) {
					list.add(element.getObjectValue());
				}
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
			Cache cache = this.getEhCache();
			for (String key : keyList) {
				Element element = cache.get(key);
				if(null != element) {
					list.add((T)element.getObjectValue());
				}
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
			Cache cache = this.getEhCache();
			for (String key : keys) {
				Element element = cache.get(key);
				if(null != element) {
					list.add((T)element.getObjectValue());
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}
	
}