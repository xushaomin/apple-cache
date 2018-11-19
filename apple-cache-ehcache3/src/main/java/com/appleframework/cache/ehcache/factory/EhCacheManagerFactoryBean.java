package com.appleframework.cache.ehcache.factory;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.ehcache.EhCacheExpiryUtil;

public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager> {

	private String name = "apple_cache";
	private String filePath = System.getProperty("user.home");

	@SuppressWarnings("deprecation")
	@Override
	public CacheManager getObject() throws Exception {
		URL xmlUrl = getClass().getResource("/ehcache.xml");
		CacheManager cacheManager = null;
		if (null == xmlUrl) {
			cacheManager = CacheManagerBuilder
					.newCacheManagerBuilder()
					.with(CacheManagerBuilder.persistence(new File(filePath, "ehcacheData")))
					.withCache(name,
							CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class,
											ResourcePoolsBuilder.newResourcePoolsBuilder()
													.heap(ConfigurationFactoryBean.getHeap(), MemoryUnit.MB)
													.offheap(ConfigurationFactoryBean.getOffheap(), MemoryUnit.MB)
													.disk(ConfigurationFactoryBean.getDisk(), MemoryUnit.MB, ConfigurationFactoryBean.isPersistent()))
									.withExpiry(EhCacheExpiryUtil.instance()))
					.build(true);
		} else {
			Configuration xmlConfig = new XmlConfiguration(xmlUrl);
			cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
			cacheManager.init();
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

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}