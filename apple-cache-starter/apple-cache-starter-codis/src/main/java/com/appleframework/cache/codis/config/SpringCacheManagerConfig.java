package com.appleframework.cache.codis.config;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.codis.spring.SpringCacheManager;

@Configuration
public class SpringCacheManagerConfig {

	@Resource
	private CodisResourcePool codisResourcePool;
	
	@Value("${spring.cache.enable:true}")
	private boolean cacheEnable = true;
	
	@Value("${spring.cache.enable:true}")
	private boolean cacheObject = true;
	
	@Value("${spring.cache.keyPrefix:springcache}")
	private String cacheKeyPrefix;
	
	private Map<String, Integer> expireConfig;
	
	@Bean
	@ConditionalOnMissingBean(SpringCacheManager.class)
	public SpringCacheManager springCacheManagerFactory() {
		SpringCacheManager springCacheManager = new SpringCacheManager();
		springCacheManager.setCodisResourcePool(codisResourcePool);
		springCacheManager.setCacheEnable(cacheEnable);
		springCacheManager.setCacheObject(cacheObject);
		springCacheManager.setCacheKeyPrefix(cacheKeyPrefix);
		springCacheManager.setExpireConfig(expireConfig);
		return springCacheManager;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
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
