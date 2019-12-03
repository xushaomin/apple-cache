package com.appleframework.cache.caffeine;

import java.io.Serializable;

import com.github.benmanes.caffeine.cache.Expiry;

public class CacheExpiryUtil {
	
	private static Expiry<String, Serializable> expiry = new CacheExpiry();
		
	public static Expiry<String, Serializable> instance() {
		return expiry;
	}
}
