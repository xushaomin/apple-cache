package com.appleframework.cache.redisson.spring;

import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {

	private RedissonClient redisson;

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
			cache = new SpringCache(redisson, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setRedisson(Redisson redisson) {
		this.redisson = redisson;
	}
}