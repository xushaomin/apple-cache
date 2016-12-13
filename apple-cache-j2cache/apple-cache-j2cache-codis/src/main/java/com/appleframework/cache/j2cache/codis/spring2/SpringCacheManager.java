package com.appleframework.cache.j2cache.codis.spring2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.spring.BaseSpringCacheManager;

import net.sf.ehcache.CacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {
	
	private Map<String, String> cacheTypeMap = new HashMap<String, String>();

	private CodisResourcePool codisResourcePool;
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
			if(cacheType.equals("codis")) {
				cache = new com.appleframework.cache.codis.spring.SpringCache(codisResourcePool, name, expire.intValue());
				cacheMap.put(name, cache);
			}
			else {
				cache = new com.appleframework.cache.ehcache.spring.SpringCache(ehcacheManager, name, expire.intValue());
				cacheMap.put(name, cache);
			}
		}
		return cache;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void setCacheTypeConfig(Map<String, String> cacheTypeMap) {
		this.cacheTypeMap = cacheTypeMap;
	}
	
}