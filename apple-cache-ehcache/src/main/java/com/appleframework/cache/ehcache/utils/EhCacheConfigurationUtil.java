package com.appleframework.cache.ehcache.utils;

import java.io.Serializable;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.ExpiryPolicy;

import com.appleframework.cache.ehcache.config.EhCacheContants;
import com.appleframework.cache.ehcache.config.EhCacheProperties;
import com.appleframework.cache.ehcache.enums.ExpiryType;

public class EhCacheConfigurationUtil  {
	
	public static CacheConfigurationBuilder<String, Serializable> initCacheConfiguration(EhCacheProperties properties) {
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
			expiryPolicy = EhCacheExpiryUtil.instance(ExpiryType.TTL, ttl);
		}
		
		if(tti > 0) {
			expiryPolicy = EhCacheExpiryUtil.instance(ExpiryType.TTI, tti);
		}
		
		if(tti <=0 && ttl <=0 ) {
			expiryPolicy = EhCacheExpiryUtil.instance();
		}
		CacheConfigurationBuilder<String, Serializable> builder = null;
		if(persistent) {
			builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class,
					ResourcePoolsBuilder.newResourcePoolsBuilder()
							.heap(heap, MemoryUnit.MB)
							.offheap(offheap, MemoryUnit.MB)
							.disk(disk, MemoryUnit.MB, true))
							.withExpiry(expiryPolicy);
		}
		else {
			builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class,
					ResourcePoolsBuilder.newResourcePoolsBuilder()
							.heap(heap, MemoryUnit.MB)
							.offheap(offheap, MemoryUnit.MB))
							.withExpiry(expiryPolicy);
		}
		return builder;
	}
		

}