package com.appleframework.cache.jedis.spring.cluster;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;
import com.appleframework.cache.jedis.factory.JedisClusterFactory;

public class SpringCacheManager extends BaseSpringCacheManager {

	private JedisClusterFactory connectionFactory;

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

	public void setConnectionFactory(JedisClusterFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
}