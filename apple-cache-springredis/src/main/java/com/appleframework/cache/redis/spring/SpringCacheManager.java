package com.appleframework.cache.redis.spring;

import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {

	private RedisTemplate<String, Object> redisTemplate;

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
			cache = new SpringCache(redisTemplate, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
}