package com.appleframework.cache.memcache.spring;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;

import net.rubyeye.xmemcached.MemcachedClient;

public class SpringCacheManager extends BaseSpringCacheManager {

	private MemcachedClient memcachedClient;

	public SpringCacheManager() {
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = cacheMap.get(name);
		if (cache == null) {
			Integer expire = expireMap.get(name);
			if (expire == null) {
				expire = 0;
				expireMap.put(name, expire);
			}
			cache = new SpringCache(memcachedClient, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

}