package com.appleframework.cache.core.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import com.appleframework.cache.core.config.SpringCacheConfig;

public abstract class BaseSpringCacheManager extends AbstractCacheManager {

	protected ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();
	protected Map<String, Integer> expireMap = new HashMap<String, Integer>();

	@Override
	protected Collection<? extends Cache> loadCaches() {
		Collection<Cache> values = cacheMap.values();
		return values;
	}

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		this.expireMap = expireConfig;
	}
	
	public void setCacheObject(Boolean isCacheObject) {
		SpringCacheConfig.setCacheObject(isCacheObject);
	}
	
	public void setCacheEnable(Boolean isCacheEnable) {
		SpringCacheConfig.setCacheEnable(isCacheEnable);
	}
	
	public void setCacheKeyPrefix(String cacheKeyPrefix) {
		SpringCacheConfig.setCacheKeyPrefix(cacheKeyPrefix);
	}

}