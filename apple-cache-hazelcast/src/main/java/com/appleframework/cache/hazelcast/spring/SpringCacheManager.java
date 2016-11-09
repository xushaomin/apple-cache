package com.appleframework.cache.hazelcast.spring;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;
import com.hazelcast.core.HazelcastInstance;

public class SpringCacheManager extends BaseSpringCacheManager {

	private HazelcastInstance hazelcastInstance;

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
			cache = new SpringCache(hazelcastInstance, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

}