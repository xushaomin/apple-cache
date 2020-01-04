package com.appleframework.cache.ehcache.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseCacheOperation;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.config.CacheConfiguration;

public class SpringCacheOperation implements BaseCacheOperation {

	private static Logger logger = LoggerFactory.getLogger(SpringCacheOperation.class);

	private String name;
	private int expire = 0;
	
	private long timeToIdleSeconds = 0L;
	private long timeToLiveSeconds = 0L;
	
	private Cache cache;
	
	private Cache getEhCache() {
		return cache;
	}
	
	private void init(CacheManager ehcacheManager){
		cache = ehcacheManager.getCache(name);
		if(null == cache) {
			try {
				ehcacheManager.addCache(name);
			} catch (ObjectExistsException e) {
				logger.warn("Cache " + name + " already exists");
			}
			cache = ehcacheManager.getCache(name);
		}
		CacheConfiguration config = cache.getCacheConfiguration();
		timeToIdleSeconds = config.getTimeToIdleSeconds();
		timeToLiveSeconds = config.getTimeToLiveSeconds();
		if(timeToIdleSeconds <= expire) {
			timeToIdleSeconds = timeToIdleSeconds + expire;
		}
		if(timeToLiveSeconds <= expire) {
			timeToLiveSeconds = timeToLiveSeconds + expire;
		}
	}
	
	public SpringCacheOperation(CacheManager ehcacheManager, String name) {
		this.name = name;
		this.init(ehcacheManager);
	}

	public SpringCacheOperation(CacheManager ehcacheManager, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.init(ehcacheManager);
	}

	public Object get(String key) {
		Object value = null;
		try {
			Element element = getEhCache().get(key);
			if(null != element) {
				if(SpringCacheConfig.isCacheObject()) {
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
			cache.setExpiredSecond(expire);
			Element element = new Element(key, cache, (int)timeToIdleSeconds, (int)timeToLiveSeconds);
			getEhCache().put(element);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}
	
	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		try {
			Element element = null;
			if(SpringCacheConfig.isCacheObject()) {
				CacheObject object = CacheObjectImpl.create(value, expire);
				element = new Element(key, object, (int)timeToIdleSeconds, (int)timeToLiveSeconds);
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
	
}
