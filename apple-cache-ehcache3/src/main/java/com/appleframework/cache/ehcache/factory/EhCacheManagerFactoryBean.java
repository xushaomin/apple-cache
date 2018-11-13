package com.appleframework.cache.ehcache.factory;

import java.net.URL;

import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.beans.factory.FactoryBean;

public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager> {
	
	@Override
	public CacheManager getObject() throws Exception {
        URL xmlUrl = getClass().getResource("/ehcaches.xml");
        CacheManager cacheManager = null;
        if(null == xmlUrl) {
        	cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        }
        else {
        	 Configuration xmlConfig = new XmlConfiguration(xmlUrl);
             cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        }
		cacheManager.init();
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

}