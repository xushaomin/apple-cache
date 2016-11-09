package com.appleframework.cache.j2cache.jedis.spring;

import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.core.spring.BaseSpringCache;
import com.appleframework.cache.jedis.factory.PoolFactory;

import net.sf.ehcache.CacheManager;

public class SpringCache extends BaseSpringCache {
	
	public SpringCache(CacheManager cacheManager, PoolFactory poolFactory, String name, int expire,
			CommandReplicator commandReplicator) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(cacheManager, poolFactory, name, expire, commandReplicator);
	}

}