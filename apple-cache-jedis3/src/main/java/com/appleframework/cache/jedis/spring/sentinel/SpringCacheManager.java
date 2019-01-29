package com.appleframework.cache.jedis.spring.sentinel;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;
import com.appleframework.cache.jedis.factory.JedisSentinelFactory;

public class SpringCacheManager extends BaseSpringCacheManager {

	private JedisSentinelFactory connectionFactory;

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
			cache = new SpringCache(connectionFactory, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setConnectionFactory(JedisSentinelFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
}