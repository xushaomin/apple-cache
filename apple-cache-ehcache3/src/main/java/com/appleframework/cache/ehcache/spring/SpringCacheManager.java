package com.appleframework.cache.ehcache.spring;

import org.ehcache.CacheManager;
import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {

	private CacheManager ehcacheManager;

	public SpringCacheManager() {
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = cacheMap.get(name);
		if (cache == null) {
			Integer expire = expireMap.get(name);
			if (expire == null) {
				expire = 0;
				expireMap.put(name, expire);
			}
			cache = new SpringCache(ehcacheManager, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

}