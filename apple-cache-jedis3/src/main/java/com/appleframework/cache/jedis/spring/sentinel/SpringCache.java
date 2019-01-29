package com.appleframework.cache.jedis.spring.sentinel;

import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseSpringCache;
import com.appleframework.cache.jedis.factory.JedisSentinelFactory;

public class SpringCache extends BaseSpringCache {

	public SpringCache(JedisSentinelFactory connectionFactory, String name, int expire) {
		this.name = name;
		if(SpringCacheConfig.isCacheObject()) {
			this.cacheOperation = new SpringCacheOperationHset(name, expire, connectionFactory);
		}
		else {
			this.cacheOperation = new SpringCacheOperationBucket(name, expire, connectionFactory);
		}
	}

}