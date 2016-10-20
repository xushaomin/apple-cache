package com.appleframework.cache.ehcache.spring;

import org.apache.log4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhcacheOperation {

	private static Logger logger = Logger.getLogger(EhcacheOperation.class);

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
	
	public EhcacheOperation(CacheManager ehcacheManager, String name) {
		this.name = name;
		this.ehcacheManager = ehcacheManager;
	}

	public EhcacheOperation(CacheManager ehcacheManager, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.ehcacheManager = ehcacheManager;
	}

	public Object get(String key) {
		Object value = null;
		try {
			Element element = getEhCache().get(key);
			if(null != element) {
				value = element.getObjectValue();
			}
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			Element element = null;
			if(expire <= 0)
				element = new Element(key, value);
			else
				element = new Element(key, value, expire, expire);
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
