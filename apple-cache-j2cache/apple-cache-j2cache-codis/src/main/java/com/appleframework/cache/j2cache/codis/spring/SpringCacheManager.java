package com.appleframework.cache.j2cache.codis.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.replicator.CommandReplicator;

import net.sf.ehcache.CacheManager;

public class SpringCacheManager extends AbstractCacheManager {

	private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();
	private Map<String, Integer> expireMap = new HashMap<String, Integer>();

	private CodisResourcePool codisResourcePool;
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
			cache = new SpringCache(ehcacheManager, codisResourcePool, name, expire.intValue(), commandReplicator);
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		this.expireMap = expireConfig;
	}
	
	public void setCacheObject(Boolean isCacheObject) {
		CacheConfig.setCacheObject(isCacheObject);
	}
	
	public void setCacheEnable(Boolean isCacheEnable) {
		CacheConfig.setCacheEnable(isCacheEnable);
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void setCommandReplicator(CommandReplicator commandReplicator) {
		this.commandReplicator = commandReplicator;
	}

}