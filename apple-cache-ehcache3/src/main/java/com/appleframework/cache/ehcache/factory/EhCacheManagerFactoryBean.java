package com.appleframework.cache.ehcache.factory;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.ehcache.EhCacheExpiryUtil;
import com.appleframework.cache.ehcache.config.EhCacheConfiguration;
import com.appleframework.cache.ehcache.config.EhCacheContants;
import com.appleframework.cache.ehcache.config.EhCacheProperties;

public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager> {

	private String name = "default";
	private String directory = System.getProperty("user.home");

	@Override
	public CacheManager getObject() throws Exception {
		URL xmlUrl = getClass().getResource("/ehcache.xml");
		CacheManager cacheManager = null;
		if (null == xmlUrl) {
			EhCacheProperties properties = null;
			Map<String, EhCacheProperties> cacheTemplate = EhCacheConfiguration.getProperties();
			if(null != cacheTemplate.get(name) ) {
				properties = cacheTemplate.get(name);
			}

			int heap = 10;
			int offheap = 100;
			int disk = 1000;
			boolean persistent = false;
			int ttl = 0;
			int tti = 0;
			ExpiryPolicy<Object, Object> expiryPolicy = null;
			if(null != properties) {
				heap = properties.getHeap();
				offheap = properties.getOffheap();
				disk = properties.getDisk();
				persistent = properties.isPersistent();
				ttl = properties.getTtl();
				tti = properties.getTti();
			}
			else {
				heap = EhCacheContants.DEFAULT_HEAP;
				offheap = EhCacheContants.DEFAULT_OFFHEAP;
				disk = EhCacheContants.DEFAULT_DISK;
				persistent = EhCacheContants.DEFAULT_PERSISTENT;
				ttl = EhCacheContants.DEFAULT_TTL;
				tti = EhCacheContants.DEFAULT_TTI;
			}
			
			if(ttl > 0) {
				expiryPolicy = EhCacheExpiryUtil.instance("ttl", ttl);
			}
			
			if(tti > 0) {
				expiryPolicy = EhCacheExpiryUtil.instance("tti", tti);
			}
			
			if(tti <=0 && tti <=0 ) {
				expiryPolicy = EhCacheExpiryUtil.instance();
			}

			cacheManager = CacheManagerBuilder
					.newCacheManagerBuilder()
					.with(CacheManagerBuilder.persistence(new File(directory, "ehcacheData")))
					.withCache(name,
							CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class,
											ResourcePoolsBuilder.newResourcePoolsBuilder()
													.heap(heap, MemoryUnit.MB)
													.offheap(offheap, MemoryUnit.MB)
													.disk(disk, MemoryUnit.MB, persistent))
									.withExpiry(expiryPolicy))
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

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}