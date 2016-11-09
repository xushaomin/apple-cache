package com.appleframework.cache.codis.spring;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.BaseSpringCache;

public class SpringCache extends BaseSpringCache {

	public SpringCache(CodisResourcePool codisResourcePool, String name, int expire) {
		this.name = name;
		if (CacheConfig.isCacheObject()) {
			this.cacheOperation = new SpringCacheOperationHset(codisResourcePool, name, expire);
		} else {
			this.cacheOperation = new SpringCacheOperationBucket(codisResourcePool, name, expire);
		}
	}

}