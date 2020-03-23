package com.appleframework.cache.ehcache3.config;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleframework.cache.ehcache.EhCacheManager;
import com.appleframework.cache.ehcache.config.EhCacheConfiguration;
import com.appleframework.cache.ehcache.config.EhCacheProperties;
import com.appleframework.cache.ehcache.spring.SpringCacheManager;
import com.appleframework.cache.ehcache.utils.EhCacheConfigurationUtil;
import com.appleframework.cache.ehcache.utils.EhCacheManagerUtil;

@Configuration
@EnableConfigurationProperties(AppleCacheProperties.class)
@ConditionalOnProperty(prefix = AppleCacheProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class AppleCacheAutoConfiguration {

	@Autowired
	private AppleCacheProperties properties;
	
	private CacheManager ehCacheManager = null;
	
	@Bean
	@ConditionalOnMissingBean
	public EhCacheConfiguration configurationFactoryBean() {
		EhCacheConfiguration bean = new EhCacheConfiguration();
		bean.setDirectory(properties.getDirectory());
		bean.setPropertiesMap(properties.getCacheTemplate());
		return bean;
	}

	@Bean("ehCacheManager")
	public CacheManager ehCacheManagerFactory() throws Exception {
		if(null != ehCacheManager) {
			return ehCacheManager;
		}
		URL xmlUrl = getClass().getResource("/ehcache.xml");
		String directory = properties.getDirectory();
		String initName = properties.getInitName();
		if (null == xmlUrl) {
			Map<String, EhCacheProperties> cacheTemplate = properties.getCacheTemplate();
			EhCacheProperties property = null;
			if(null != cacheTemplate) {
				property = properties.getCacheTemplate().get(initName);
			}
			CacheConfigurationBuilder<String, Serializable> configuration = EhCacheConfigurationUtil.initCacheConfiguration(property);			
			ehCacheManager = EhCacheManagerUtil.initCacheManager(initName, directory, configuration);

		} else {
			ehCacheManager = EhCacheManagerUtil.initCacheManager(xmlUrl);
		}
		return ehCacheManager;
	}

	
	@Bean
	@ConditionalOnMissingBean(SpringCacheManager.class)
	public SpringCacheManager springCacheManagerFactory() throws Exception {
		SpringCacheManager springCacheManager = new SpringCacheManager();
		if(null == ehCacheManager) {
			ehCacheManagerFactory();
		}
		springCacheManager.setEhcacheManager(ehCacheManager);
		Map<String, Integer> expireConfig = new HashMap<String, Integer>();
		Map<String, EhCacheProperties> cacheTemplate = properties.getCacheTemplate();
		if(null != cacheTemplate) {
			for (Map.Entry<String, EhCacheProperties> map : properties.getCacheTemplate().entrySet()) {
				String key = map.getKey();
				EhCacheProperties property = map.getValue();
				if(property.isSpringCache()) {
					expireConfig.put(key, property.getTti());
				}
			}	
			springCacheManager.setExpireConfig(expireConfig);
		}
		return springCacheManager;
	}
	
	@Bean("ehCache3Manager")
	@ConditionalOnBean(org.ehcache.CacheManager.class)
	public com.appleframework.cache.core.CacheManager appleCacheManagerFactory() throws Exception {
		EhCacheManager ehCache3Manager = new EhCacheManager();
		if(null == ehCacheManager) {
			ehCacheManagerFactory();
		}
		String name = properties.getInitName();
		ehCache3Manager.setName(name);
		ehCache3Manager.setEhcacheManager(ehCacheManager);
		return ehCache3Manager;
	}

}
