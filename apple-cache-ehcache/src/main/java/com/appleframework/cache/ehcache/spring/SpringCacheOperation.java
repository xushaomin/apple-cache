package com.appleframework.cache.ehcache.spring;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class SpringCacheOperation implements CacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperation.class);

	private String name;
	private int expire = 0;
	
	private CacheManager ehcacheManager;
	
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
	
	public SpringCacheOperation(CacheManager ehcacheManager, String name) {
		this.name = name;
		this.ehcacheManager = ehcacheManager;
	}

	public SpringCacheOperation(CacheManager ehcacheManager, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.ehcacheManager = ehcacheManager;
	}

	public Object get(String key) {
		Object value = null;
		try {
			Element element = getEhCache().get(key);
			if(null != element) {
				if(CacheConfig.isCacheObject) {
					CacheObject cache = (CacheObject) element.getObjectValue();
					if (null != cache) {
						if (cache.isExpired()) {
							this.resetCacheObject(key, cache);
						} else {
							value = cache.getObject();
						}
					}
				}
				else {
					value = element.getObjectValue();
				}
			}
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
		return value;
	}

	private void resetCacheObject(String key, CacheObject cache) {
		try {
			cache.setExpiredTime(getExpiredTime());
			Element element = new Element(key, cache);
			getEhCache().put(element);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}
	
	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			Element element = null;
			if(CacheConfig.isCacheObject) {
				CacheObject object = new CacheObjectImpl(value, getExpiredTime());
				element = new Element(key, object);
			}
			else {
				if(expire > 0)
					element = new Element(key, value, expire, expire);
				else
					element = new Element(key, value);
			}
			getEhCache().put(element);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void clear() {
		try {
			getEhCache().removeAll();
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void delete(String key) {
		try {
			getEhCache().remove(key);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public int getExpire() {
		return expire;
	}
	
	private long getExpiredTime() {
		long lastTime = 2592000000L;
		if (expire > 0) {
			lastTime = expire * 1000;
		}
		return System.currentTimeMillis() + lastTime;
	}
	
}
