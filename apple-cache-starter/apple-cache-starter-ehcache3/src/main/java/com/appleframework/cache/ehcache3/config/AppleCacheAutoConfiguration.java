package com.appleframework.cache.ehcache3.config;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleframework.cache.ehcache.EhCacheExpiryUtil;
import com.appleframework.cache.ehcache.EhCacheManager;
import com.appleframework.cache.ehcache.factory.ConfigurationFactoryBean;
import com.appleframework.cache.ehcache.spring.SpringCacheManager;


@Configuration
@EnableConfigurationProperties(AppleCacheProperties.class)
//@AutoConfigureBefore(DataSourceAutoConfiguration.class)
//@Import(DruidDynamicDataSourceConfiguration.class)
@ConditionalOnProperty(prefix = AppleCacheProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class AppleCacheAutoConfiguration {

	@Autowired
	private AppleCacheProperties properties;
	
	private CacheManager ehCacheManager = null;
	
	@Bean
	@ConditionalOnMissingBean
	public ConfigurationFactoryBean configurationFactoryBean() {
		ConfigurationFactoryBean bean = new ConfigurationFactoryBean();
		bean.setDisk(properties.getDisk());
		bean.setHeap(properties.getHeap());
		bean.setName(properties.getName());
		bean.setOffheap(properties.getOffheap());
		bean.setPersistent(properties.getPersistent());		
		return bean;
	}

	@Bean
	@ConditionalOnMissingBean(CacheManager.class)
	public CacheManager ehCacheManagerFactory() throws Exception {
		URL xmlUrl = getClass().getResource("/ehcache.xml");
		String filePath = properties.getFilePath();
		if (null == xmlUrl) {
			ehCacheManager = CacheManagerBuilder
					.newCacheManagerBuilder()
					.with(CacheManagerBuilder.persistence(new File(filePath, "ehcacheData")))
					.withCache(ConfigurationFactoryBean.getName(),
							CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class,
											ResourcePoolsBuilder.newResourcePoolsBuilder()
													.heap(ConfigurationFactoryBean.getHeap(), MemoryUnit.MB)
													.offheap(ConfigurationFactoryBean.getOffheap(), MemoryUnit.MB)
													.disk(ConfigurationFactoryBean.getDisk(), MemoryUnit.MB, ConfigurationFactoryBean.isPersistent()))
									.withExpiry(EhCacheExpiryUtil.instance()))
					.build(true);
		} else {
			org.ehcache.config.Configuration xmlConfig = new XmlConfiguration(xmlUrl);
			ehCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
			ehCacheManager.init();
		}

		return ehCacheManager;
	}

	
	@Bean
	@ConditionalOnMissingBean(SpringCacheManager.class)
	public SpringCacheManager springCacheManagerFactory() throws Exception {
		configurationFactoryBean();
		SpringCacheManager springCacheManager = new SpringCacheManager();
		springCacheManager.setEhcacheManager(ehCacheManagerFactory());
		springCacheManager.setCacheEnable(properties.getCacheEnable());
		springCacheManager.setCacheObject(properties.getCacheObject());
		springCacheManager.setCacheKeyPrefix(properties.getCacheKeyPrefix());
		springCacheManager.setExpireConfig(properties.getExpireConfig());
		return springCacheManager;
	}
	
	@Bean
	@ConditionalOnMissingBean(com.appleframework.cache.core.CacheManager.class)
	public com.appleframework.cache.core.CacheManager appleCacheManagerFactory() throws Exception {
		EhCacheManager cacheManager = new EhCacheManager();
		if(null == ehCacheManager) {
			configurationFactoryBean();
		}
		cacheManager.setName(properties.getName());
		cacheManager.setEhcacheManager(ehCacheManager);
		return cacheManager;
	}

}
