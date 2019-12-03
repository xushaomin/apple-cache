package com.appleframework.cache.caffeine.spring;

import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.spring.BaseSpringCache;

public class SpringCache extends BaseSpringCache {

	public SpringCache(CacheManager cacheManager, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(cacheManager, name, expire);
	}

}