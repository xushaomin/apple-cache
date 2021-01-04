package com.appleframework.cache.ehcache.utils;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;


public class EhCacheManagerUtil  {
	
	public static CacheManager initCacheManager(String name, String directory,
			CacheConfigurationBuilder<String, Serializable> cacheConfiguration) {
		CacheManager cacheManager = null;
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(new File(directory, "ehcacheData")))
				.withCache(name, cacheConfiguration)
				.build(true);
		return cacheManager;
	}
	
	public static CacheManager initCacheManager(String name,
			CacheConfigurationBuilder<String, Serializable> cacheConfiguration) {
		CacheManager cacheManager = null;
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder()
				.withCache(name, cacheConfiguration)
				.build(true);
		return cacheManager;
	}

	
	public static CacheManager initCacheManager(URL xmlUrl) {
		Configuration xmlConfig = new XmlConfiguration(xmlUrl);
		CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		cacheManager.init();
		return cacheManager;
	}
	

}