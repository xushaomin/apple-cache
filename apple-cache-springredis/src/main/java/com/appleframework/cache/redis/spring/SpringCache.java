package com.appleframework.cache.redis.spring;

import org.springframework.data.redis.core.RedisTemplate;

import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseSpringCache;

public class SpringCache extends BaseSpringCache {

	public SpringCache(RedisTemplate<String, Object> redisTemplate, String name, int expire) {
		this.name = name;
		if(SpringCacheConfig.isCacheObject()) {
			this.cacheOperation = new SpringCacheOperationHset(name, expire, redisTemplate);
		}
		else {
			this.cacheOperation = new SpringCacheOperationBucket(name, expire, redisTemplate);
		}
	}

}