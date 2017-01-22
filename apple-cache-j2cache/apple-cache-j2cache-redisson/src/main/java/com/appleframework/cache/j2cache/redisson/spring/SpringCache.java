package com.appleframework.cache.j2cache.redisson.spring;

import org.redisson.api.RedissonClient;

import com.appleframework.cache.core.spring.BaseSpringCache;

import net.sf.ehcache.CacheManager;

public class SpringCache extends BaseSpringCache {

	public SpringCache(CacheManager cacheManager, RedissonClient redisson, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(cacheManager, redisson, name, expire);
	}

}