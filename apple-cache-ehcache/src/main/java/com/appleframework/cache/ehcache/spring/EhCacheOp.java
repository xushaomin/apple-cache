package com.appleframework.cache.ehcache.spring;

import org.apache.log4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhCacheOp {

	private static Logger logger = Logger.getLogger(EhCacheOp.class);

	private String name;
	private int expire;
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

	public EhCacheOp(String name, int expire, CacheManager ehcacheManager) {
		this.name = name;
		this.expire = expire;
		this.ehcacheManager = ehcacheManager;
	}
	
	public EhCacheOp(String name, CacheManager ehcacheManager) {
		this.name = name;
		this.expire = 0;
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
			logger.warn("获取 Cache 缓存错误", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			Element element = new Element(key, value);
			getEhCache().put(element);
		} catch (Exception e) {
			logger.warn("更新 Cache 缓存错误", e);
		}
	}

	public void clear() {
		try {
			getEhCache().removeAll();
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		}
	}

	public void delete(String key) {
		try {
			getEhCache().remove(key);
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		}
	}

	public int getExpire() {
		return expire;
	}
	
}
