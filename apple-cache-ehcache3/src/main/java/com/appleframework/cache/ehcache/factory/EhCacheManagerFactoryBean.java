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
import com.appleframework.cache.ehcache.config.EhCacheConfiguration;
import com.appleframework.cache.ehcache.config.EhCacheContants;
import com.appleframework.cache.ehcache.config.EhCacheProperties;

public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager> {

	private String name = "default";
	private String filePath = System.getProperty("user.home");

	@Override
	public CacheManager getObject() throws Exception {
		URL xmlUrl = getClass().getResource("/ehcache.xml");
		CacheManager cacheManager = null;
		if (null == xmlUrl) {
			EhCacheProperties properties = EhCacheConfiguration.getProperties().get("default");
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
			cacheManager = CacheManagerBuilder
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