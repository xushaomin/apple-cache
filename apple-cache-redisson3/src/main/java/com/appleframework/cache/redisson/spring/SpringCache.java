package com.appleframework.cache.redisson.spring;

import org.redisson.api.RedissonClient;

import com.appleframework.cache.core.spring.BaseSpringCache;

public class SpringCache extends BaseSpringCache {

	public SpringCache(RedissonClient redisson, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(redisson, name, expire);
	}
	
}