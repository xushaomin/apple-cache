package com.appleframework.cache.ehcache3.config;

import java.util.Map;

import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleframework.cache.ehcache.spring.SpringCacheManager;

@Configuration
public class SpringCacheManagerConfig {
	
	@Value("${spring.cache.enable:true}")
	private boolean cacheEnable = true;
	
	@Value("${spring.cache.oject:true}")
	private boolean cacheObject = true;
	
	@Value("${spring.cache.keyPrefix:springcache}")
	private String cacheKeyPrefix;
	
	private Map<String, Integer> expireConfig;
	
	@Bean
	@ConditionalOnMissingBean(SpringCacheManager.class)
	public SpringCacheManager springCacheManagerFactory(CacheManager ehcacheManager) {
		SpringCacheManager springCacheManager = new SpringCacheManager();
		springCacheManager.setEhcacheManager(ehcacheManager);
		springCacheManager.setCacheEnable(cacheEnable);
		springCacheManager.setCacheObject(cacheObject);
		springCacheManager.setCacheKeyPrefix(cacheKeyPrefix);
		springCacheManager.setExpireConfig(expireConfig);
		return springCacheManager;
	}
	
	public void setCacheEnable(boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
	}

	public void setCacheObject(boolean cacheObject) {
		this.cacheObject = cacheObject;
	}

	public void setCacheKeyPrefix(String cacheKeyPrefix) {
		this.cacheKeyPrefix = cacheKeyPrefix;
	}

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		this.expireConfig = expireConfig;
	}
	
}
