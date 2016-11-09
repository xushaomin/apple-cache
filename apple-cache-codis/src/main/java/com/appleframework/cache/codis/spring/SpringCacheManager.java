package com.appleframework.cache.codis.spring;

import org.springframework.cache.Cache;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.spring.BaseSpringCacheManager;

public class SpringCacheManager extends BaseSpringCacheManager {

	private CodisResourcePool codisResourcePool;

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
			cache = new SpringCache(codisResourcePool, name, expire.intValue());
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

}