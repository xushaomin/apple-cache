package com.appleframework.cache.ehcache;

public class EhCacheExpiryUtil {
	
	private static EhCacheExpiry ehCacheExpiry = new EhCacheExpiry();
		
	public static EhCacheExpiry instance() {
		return ehCacheExpiry;
	}
}
