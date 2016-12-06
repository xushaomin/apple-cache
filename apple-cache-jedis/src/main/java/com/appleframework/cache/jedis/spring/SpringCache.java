package com.appleframework.cache.jedis.spring;

import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseSpringCache;
import com.appleframework.cache.jedis.factory.PoolFactory;

public class SpringCache extends BaseSpringCache {

	public SpringCache(PoolFactory poolFactory, String name, int expire) {
		this.name = name;
		if(SpringCacheConfig.isCacheObject()) {
			this.cacheOperation = new SpringCacheOperationHset(name, expire, poolFactory);
		}
		else {
			this.cacheOperation = new SpringCacheOperationBucket(name, expire, poolFactory);
		}
	}

}