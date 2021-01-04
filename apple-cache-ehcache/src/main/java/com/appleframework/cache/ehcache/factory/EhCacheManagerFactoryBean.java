package com.appleframework.cache.ehcache.factory;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.ehcache.config.EhCacheConfiguration;
import com.appleframework.cache.ehcache.config.EhCacheProperties;
import com.appleframework.cache.ehcache.utils.EhCacheConfigurationUtil;
import com.appleframework.cache.ehcache.utils.EhCacheManagerUtil;

public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager> {

	private String name = "default";
	private String directory = System.getProperty("user.home");

	@Override
	public CacheManager getObject() throws Exception {
		URL xmlUrl = getClass().getResource("/ehcache.xml");
		CacheManager cacheManager = null;
		if (null == xmlUrl) {
			EhCacheProperties properties = null;
			boolean persistent = false;
			Map<String, EhCacheProperties> cacheTemplate = EhCacheConfiguration.getProperties();
			if (null != cacheTemplate.get(name)) {
				properties = cacheTemplate.get(name);
				if(null != properties) {
					persistent = properties.isPersistent();
				}
			}
			CacheConfigurationBuilder<String, Serializable> builder = EhCacheConfigurationUtil
					.initCacheConfiguration(properties);
			
			if(persistent) {
				cacheManager = EhCacheManagerUtil.initCacheManager(name, directory, builder);
			}
			else {
				cacheManager = EhCacheManagerUtil.initCacheManager(name, builder);
			}
		} else {
			cacheManager = EhCacheManagerUtil.initCacheManager(xmlUrl);
		}

		return cacheManager;
	}

	@Override
	public Class<?> getObjectType() {
		return CacheManager.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}