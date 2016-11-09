package com.appleframework.cache.memcache.spring;

import com.appleframework.cache.core.spring.BaseSpringCache;

import net.rubyeye.xmemcached.MemcachedClient;

public class SpringCache extends BaseSpringCache {
	
	public SpringCache(MemcachedClient memcachedClient, String name, int expire) {
		this.name = name;
		this.cacheOperation = new SpringCacheOperation(memcachedClient, name, expire);
	}
	
}