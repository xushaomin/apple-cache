package com.appleframework.cache.ehcache;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.ehcache.expiry.ExpiryPolicy;

public class EhCacheExpiryUtil {
	
	private static ExpiryPolicy<Object, Object> ehCacheExpiry = null;
		
	public static ExpiryPolicy<Object, Object> instance() {
		if(null == ehCacheExpiry) {
			ehCacheExpiry = ExpiryPolicyBuilder2.noExpiration();
		}
		return ehCacheExpiry;
	}
	
	public static ExpiryPolicy<Object, Object> instance(String type, long ttl) {
		if(null == ehCacheExpiry) {
			synchronized (type) {
				if(type.equalsIgnoreCase("ttl")) {
					ehCacheExpiry = ExpiryPolicyBuilder2.timeToLiveExpiration(Duration.of(ttl, ChronoUnit.SECONDS));

				}
				else if(type.equalsIgnoreCase("tti")) {
					ehCacheExpiry = ExpiryPolicyBuilder2.timeToIdleExpiration(Duration.of(ttl, ChronoUnit.SECONDS));
				}
				else {
					ehCacheExpiry = ExpiryPolicyBuilder2.noExpiration();
				}
			}
		}
		return ehCacheExpiry;
	}
	
	public static void setExpiry(String key, int seconds) {
		ExpiryPolicyBuilder2.setExpiry(key, seconds);
	}
}
