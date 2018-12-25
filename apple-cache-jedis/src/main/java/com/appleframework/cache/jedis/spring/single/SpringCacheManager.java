package com.appleframework.cache.jedis.spring.single;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;
import redis.clients.jedis.JedisPool;

public class SpringCacheManager extends BaseSpringCacheManager {

	private JedisPool jedisPool;

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
			cache = new SpringCache(jedisPool, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
}