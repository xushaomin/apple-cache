package com.appleframework.cache.j2cache.jedis.spring;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.core.spring.BaseSpringCacheManager;
import com.appleframework.cache.jedis.factory.PoolFactory;

import net.sf.ehcache.CacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {

	private PoolFactory poolFactory;
	private CacheManager ehcacheManager;
	private CommandReplicator commandReplicator;

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
			cache = new SpringCache(ehcacheManager, poolFactory, name, expire.intValue(), commandReplicator);
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setPoolFactory(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setCommandReplicator(CommandReplicator commandReplicator) {
		this.commandReplicator = commandReplicator;
	}

}