package com.appleframework.cache.j2cache.codis.spring2.codis;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseSpringCache;

public class CodisSpringCache extends BaseSpringCache {

	public CodisSpringCache(CodisResourcePool codisResourcePool, String name, int expire) {
		this.name = name;
		if (SpringCacheConfig.isCacheObject()) {
			this.cacheOperation = new CodisSpringCacheOperationHset(codisResourcePool, name, expire);
		} else {
			this.cacheOperation = new CodisSpringCacheOperationBucket(codisResourcePool, name, expire);
		}
	}

}