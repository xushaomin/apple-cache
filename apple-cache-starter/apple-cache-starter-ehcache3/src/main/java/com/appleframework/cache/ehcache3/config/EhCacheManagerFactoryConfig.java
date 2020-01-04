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
import com.appleframework.cache.ehcache.config.EhCacheConfiguration;
import com.appleframework.cache.ehcache.config.EhCacheContants;
import com.appleframework.cache.ehcache.config.EhCacheProperties;

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
			String name = "default";
			EhCacheProperties properties = EhCacheConfiguration.getProperties().get(name);
			int heap = 10;
			int offheap = 100;
			int disk = 1000;
			boolean persistent = false;
			if(null != properties) {
				heap = properties.getHeap();
				offheap = properties.getOffheap();
				disk = properties.getDisk();
				persistent = properties.isPersistent();
			}
			else {
				heap = EhCacheContants.DEFAULT_HEAP;
				offheap = EhCacheContants.DEFAULT_OFFHEAP;
				disk = EhCacheContants.DEFAULT_DISK;
				persistent = EhCacheContants.DEFAULT_PERSISTENT;
			}
			ehCacheManager = CacheManagerBuilder
					.newCacheManagerBuilder()
					.with(CacheManagerBuilder.persistence(new File(filePath, "ehcacheData")))
					.withCache(name,
							CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class,
											ResourcePoolsBuilder.newResourcePoolsBuilder()
													.heap(heap, MemoryUnit.MB)
													.offheap(offheap, MemoryUnit.MB)
													.disk(disk, MemoryUnit.MB, persistent))
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
