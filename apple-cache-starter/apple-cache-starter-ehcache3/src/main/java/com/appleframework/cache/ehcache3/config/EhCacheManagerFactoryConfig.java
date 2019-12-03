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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.appleframework.cache.ehcache.EhCacheExpiryUtil;
import com.appleframework.cache.ehcache.factory.ConfigurationFactoryBean;

@Configuration
@Order(2)
public class EhCacheManagerFactoryConfig {	
	
	@Value("${spring.ehcache.config.filePath:null}")
	private String filePath;

	@Bean
	@ConditionalOnMissingBean(CacheManager.class)
	public CacheManager ehCacheManagerFactory() throws Exception {
		URL xmlUrl = getClass().getResource("/ehcache.xml");
		CacheManager ehCacheManager = null;
		if (null == xmlUrl) {
			if("null".equals(filePath)) {
				filePath = System.getProperty("java.home");
			}
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
	
}
