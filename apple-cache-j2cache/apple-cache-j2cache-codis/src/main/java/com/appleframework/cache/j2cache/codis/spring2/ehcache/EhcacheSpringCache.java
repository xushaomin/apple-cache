package com.appleframework.cache.j2cache.codis.spring2.ehcache;

import com.appleframework.cache.core.spring.BaseSpringCache;

import net.sf.ehcache.CacheManager;

public class EhcacheSpringCache extends BaseSpringCache {

	public EhcacheSpringCache(CacheManager cacheManager, String name, int expire) {
		this.name = name;
		this.cacheOperation = new EhcacheSpringCacheOperation(cacheManager, name, expire);
	}

}