package com.appleframework.cache.jedis.spring;

import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.BaseSpringCache;

import redis.clients.jedis.JedisPool;

public class SpringCache extends BaseSpringCache {

	public SpringCache(JedisPool jedisPool, String name, int expire) {
		this.name = name;
		if(CacheConfig.isCacheObject()) {
			this.cacheOperation = new SpringCacheOperationHset(name, expire, jedisPool);
		}
		else {
			this.cacheOperation = new SpringCacheOperationBucket(name, expire, jedisPool);
		}
	}

}