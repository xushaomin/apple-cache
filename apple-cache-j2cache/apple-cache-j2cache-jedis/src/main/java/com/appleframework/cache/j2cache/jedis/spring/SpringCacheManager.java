package com.appleframework.cache.j2cache.jedis.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.jedis.factory.PoolFactory;

import net.sf.ehcache.CacheManager;

public class SpringCacheManager extends AbstractCacheManager {

	private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();
	private Map<String, Integer> expireMap = new HashMap<String, Integer>();
	private Map<String, Boolean> openMap = new HashMap<String, Boolean>();

	private PoolFactory poolFactory;
	private CacheManager ehcacheManager;
	private CommandReplicator commandReplicator;

	public SpringCacheManager() {
	}

	@Override
	protected Collection<? extends Cache> loadCaches() {
		Collection<Cache> values = cacheMap.values();
		return values;
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
			Boolean isOpen = openMap.get(name);
			if (isOpen == null) {
				isOpen = true;
				openMap.put(name, isOpen);
			}
			if(!isOpen) {
				cache = new SpringCache(ehcacheManager, poolFactory, name, expire.intValue(), false, commandReplicator);
			}
			else {
				cache = new SpringCache(ehcacheManager, poolFactory, name, expire.intValue(), true, commandReplicator);
			}
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

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		this.expireMap = expireConfig;
	}

	public void setOpenConfig(Map<String, Boolean> openConfig) {
		this.openMap = openConfig;
	}
	
	public void setCacheObject(Boolean isCacheObject) {
		CacheConfig.isCacheObject = isCacheObject;
	}

	public void setCommandReplicator(CommandReplicator commandReplicator) {
		this.commandReplicator = commandReplicator;
	}

}