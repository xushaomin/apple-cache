package com.appleframework.cache.codis.config;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleframework.cache.codis.CodisBucketCacheManager;
import com.appleframework.cache.codis.CodisHmsetCacheManager;
import com.appleframework.cache.codis.CodisHsetCacheManager;
import com.appleframework.cache.codis.CodisResourcePool;

@Configuration
public class CodisCacheManagerConfig {

	@Resource
	private CodisResourcePool codisResourcePool;

	@Bean
	@ConditionalOnMissingBean(CodisBucketCacheManager.class)
	public CodisBucketCacheManager codisBucketCacheManagerFactory() {
		CodisBucketCacheManager cacheManager = new CodisBucketCacheManager();
		cacheManager.setCodisResourcePool(codisResourcePool);
		return cacheManager;
	}

	@Bean
	@ConditionalOnMissingBean(CodisHmsetCacheManager.class)
	public CodisHmsetCacheManager codisHmsetCacheManagerFactory() {
		CodisHmsetCacheManager cacheManager = new CodisHmsetCacheManager();
		cacheManager.setCodisResourcePool(codisResourcePool);
		return cacheManager;
	}

	@Bean
	@ConditionalOnMissingBean(CodisHsetCacheManager.class)
	public CodisHsetCacheManager codisHsetCacheManagerFactory() {
		CodisHsetCacheManager cacheManager = new CodisHsetCacheManager();
		cacheManager.setCodisResourcePool(codisResourcePool);
		return cacheManager;
	}

}
