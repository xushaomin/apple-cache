package com.appleframework.cache.codis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleframework.cache.codis.CodisBucketCacheManager;
import com.appleframework.cache.codis.CodisHmsetCacheManager;
import com.appleframework.cache.codis.CodisHsetCacheManager;
import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.codis.spring.SpringCacheManager;

import io.codis.jodis.RoundRobinJedisPool;


@Configuration
@EnableConfigurationProperties(AppleCacheProperties.class)
//@AutoConfigureBefore(DataSourceAutoConfiguration.class)
//@Import(DruidDynamicDataSourceConfiguration.class)
@ConditionalOnProperty(prefix = AppleCacheProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class AppleCacheAutoConfiguration {

	@Autowired
	private AppleCacheProperties properties;
	
	private CodisResourcePool codisResourcePool = null;
	
	@Bean
	@ConditionalOnMissingBean(RoundRobinJedisPool.class)
	public CodisResourcePool codisResourcePoolFactory() {
		CodisResourcePool pool = new CodisResourcePool();
		pool.setDatabase(properties.getDatabase());
		pool.setMaxIdle(properties.getMaxIdle());
		pool.setMaxTotal(properties.getMaxTotal());
		pool.setMinIdle(properties.getMinIdle());
		pool.setPassword(properties.getPassword());
		pool.setTestOnBorrow(properties.isTestOnBorrow());
		pool.setTestOnCreate(properties.isTestOnReturn());
		pool.setTestOnReturn(properties.isTestWhileIdle());
		pool.setTestWhileIdle(properties.isTestWhileIdle());
		pool.setTimeoutMs(properties.getTimeoutMs());
		pool.setZkAddr(properties.getZkAddr());
		pool.setZkProxyDir(properties.getZkAddr());
		pool.setZkSessionTimeoutMs(properties.getZkSessionTimeoutMs());
		pool.init();
		return pool;
	}
	
	@Bean(name = "codisBucketCacheManager")
	@ConditionalOnMissingBean(CodisBucketCacheManager.class)
	public CodisBucketCacheManager codisBucketCacheManagerFactory() {
		CodisBucketCacheManager cacheManager = new CodisBucketCacheManager();
		if(null == codisResourcePool) {
			codisResourcePoolFactory();
		}
		cacheManager.setCodisResourcePool(codisResourcePool);
		return cacheManager;
	}

	@Bean(name = "codisHmsetCacheManager")
	@ConditionalOnMissingBean(CodisHmsetCacheManager.class)
	public CodisHmsetCacheManager codisHmsetCacheManagerFactory() {
		CodisHmsetCacheManager cacheManager = new CodisHmsetCacheManager();
		if(null == codisResourcePool) {
			codisResourcePoolFactory();
		}
		cacheManager.setCodisResourcePool(codisResourcePool);
		return cacheManager;
	}

	@Bean(name = "codisHsetCacheManager")
	@ConditionalOnMissingBean(CodisHsetCacheManager.class)
	public CodisHsetCacheManager codisHsetCacheManagerFactory() {
		CodisHsetCacheManager cacheManager = new CodisHsetCacheManager();
		if(null == codisResourcePool) {
			codisResourcePoolFactory();
		}
		cacheManager.setCodisResourcePool(codisResourcePool);
		return cacheManager;
	}
	
	@Bean
	@ConditionalOnMissingBean(SpringCacheManager.class)
	public SpringCacheManager springCacheManagerFactory() throws Exception {
		if(null == codisResourcePool) {
			codisResourcePoolFactory();
		}
		SpringCacheManager springCacheManager = new SpringCacheManager();
		springCacheManager.setCodisResourcePool(codisResourcePool);
		springCacheManager.setCacheEnable(properties.isCacheEnable());
		springCacheManager.setCacheObject(properties.isCacheObject());
		springCacheManager.setCacheKeyPrefix(properties.getCacheKeyPrefix());
		springCacheManager.setExpireConfig(properties.getExpireConfig());
		return springCacheManager;
	}

}
