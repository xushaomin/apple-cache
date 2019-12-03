package com.appleframework.cache.caffeine.spring;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.spring.BaseSpringCacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {

	private CacheManager cacheManager;

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
			cache = new SpringCache(cacheManager, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void destory() {
	}

}