package com.appleframework.cache.hazelcast.spring;

import com.appleframework.cache.core.spring.BaseSpringCache;
import com.hazelcast.core.HazelcastInstance;

public class SpringCache extends BaseSpringCache {
	
	public SpringCache(HazelcastInstance instance, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(instance, name, expire);
	}
	
}