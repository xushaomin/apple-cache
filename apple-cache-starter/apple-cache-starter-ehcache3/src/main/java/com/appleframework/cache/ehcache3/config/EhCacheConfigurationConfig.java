package com.appleframework.cache.ehcache3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.appleframework.cache.ehcache.factory.ConfigurationFactoryBean;

@Configuration
@Order(1)
public class EhCacheConfigurationConfig {

	@Value("${spring.ehcache.config.name:applecache}")
	private String name = "apple_cache";
	
	@Value("${spring.ehcache.config.heap:10}")
	private Integer heap = 10;
	
	@Value("${spring.ehcache.config.offheap:100}")
	private Integer offheap = 100;
	
	@Value("${spring.ehcache.config.disk:1000}")
	private Integer disk = 1000;
	
	@Value("${spring.ehcache.config.persistent:false}")
	private Boolean persistent = false;
		
	@Bean
	@ConditionalOnMissingBean(ConfigurationFactoryBean.class)
	public ConfigurationFactoryBean configurationFactory() {
		ConfigurationFactoryBean factoryBean = new ConfigurationFactoryBean();
		factoryBean.setHeap(heap);
		factoryBean.setOffheap(offheap);
		factoryBean.setDisk(disk);
		factoryBean.setName(name);
		factoryBean.setPersistent(persistent);
		return factoryBean;
	}
	
}
