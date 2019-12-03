package com.appleframework.cache.ehcache;

import java.io.Serializable;

import org.ehcache.expiry.ExpiryPolicy;

public class EhCacheExpiryUtil {
	
	private static ExpiryPolicy<String, Serializable> ehCacheExpiry = new EhCacheExpiry();
		
	public static ExpiryPolicy<String, Serializable> instance() {
		return ehCacheExpiry;
	}
}
