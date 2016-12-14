package com.appleframework.cache.j2cache.jedis.spring2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;

import com.appleframework.cache.core.spring.BaseSpringCacheManager;
import com.appleframework.cache.jedis.factory.PoolFactory;

import net.sf.ehcache.CacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {
	
	private Map<String, String> cacheTypeMap = new HashMap<String, String>();

	private PoolFactory poolFactory;
	private CacheManager ehcacheManager;

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
			String cacheType = cacheTypeMap.get(name);
			if(null != cacheType && cacheType.equals("ehcache")) {
				cache = new com.appleframework.cache.ehcache.spring.SpringCache(ehcacheManager, name, expire.intValue());
				cacheMap.put(name, cache);
			}
			else {
				cache = new com.appleframework.cache.jedis.spring.SpringCache(poolFactory, name, expire.intValue());
				cacheMap.put(name, cache);
			}
		}
		return cache;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setPoolFactory(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	public void setCacheTypeConfig(Map<String, String> cacheTypeMap) {
		this.cacheTypeMap = cacheTypeMap;
	}
	
}