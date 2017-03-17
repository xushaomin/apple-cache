package com.appleframework.cache.ehcache.factory;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.beans.factory.FactoryBean;

public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager> {

	@Override
	public CacheManager getObject() throws Exception {
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
		cacheManager.init();
		return cacheManager;
	}

	@Override
	public Class<?> getObjectType() {
		return CacheManager.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}