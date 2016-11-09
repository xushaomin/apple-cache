package com.appleframework.cache.ehcache.spring;

import com.appleframework.cache.core.spring.BaseSpringCache;

import net.sf.ehcache.CacheManager;

public class SpringCache extends BaseSpringCache {

	public SpringCache(CacheManager cacheManager, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(cacheManager, name, expire);
	}

}