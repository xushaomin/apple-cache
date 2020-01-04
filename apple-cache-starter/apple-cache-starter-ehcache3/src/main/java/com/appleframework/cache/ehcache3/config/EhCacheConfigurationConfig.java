package com.appleframework.cache.ehcache3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.appleframework.cache.ehcache.config.EhCacheConfiguration;
import com.appleframework.cache.ehcache.config.EhCacheProperties;

@Configuration
@Order(1)
public class EhCacheConfigurationConfig {

	private String name = "default";
	
	@Value("${spring.ehcache.default.heap:10}")
	private Integer heap = 10;
	
	@Value("${spring.ehcache.default.offheap:100}")
	private Integer offheap = 100;
	
	@Value("${spring.ehcache.default.disk:1000}")
	private Integer disk = 1000;
	
	@Value("${spring.ehcache.default.persistent:false}")
	private Boolean persistent = false;
	
	@Value("${spring.ehcache.config.filePath}")
	private static String filePath = System.getProperty("user.home");
	
	@Value("${spring.ehcache.config.cache-enable:true}")
	private static Boolean cacheEnable = true;
	
	@Value("${spring.ehcache.config.cache-object:false}")
	private static Boolean cacheObject = false;

	@Bean
	@ConditionalOnMissingBean(EhCacheConfiguration.class)
	public EhCacheConfiguration configurationFactory() {
		EhCacheConfiguration factoryBean = new EhCacheConfiguration();
		factoryBean.setCacheEnable(cacheEnable);
		factoryBean.setCacheObject(cacheObject);
		factoryBean.setFilePath(filePath);
		
		EhCacheProperties property = new EhCacheProperties();
		property.setDisk(disk);
		property.setHeap(heap);
		property.setOffheap(offheap);
		property.setPersistent(persistent);
		
		factoryBean.setProperties(name, property);
		return factoryBean;
	}
	
}
