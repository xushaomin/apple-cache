package com.appleframework.cache.ehcache3.config;

import javax.annotation.Resource;

import org.ehcache.CacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.appleframework.cache.ehcache.EhCacheManager;

@Configuration
@Order(3)
public class EhCacheManagerConfig {

	@Resource
	private CacheManager ehCacheManager;
	
	@Bean
	@ConditionalOnMissingBean(com.appleframework.cache.core.CacheManager.class)
	public com.appleframework.cache.core.CacheManager cacheManagerFactory() {
		EhCacheManager cacheManager = new EhCacheManager();
		cacheManager.setEhcacheManager(ehCacheManager);
		return cacheManager;
	}

}
