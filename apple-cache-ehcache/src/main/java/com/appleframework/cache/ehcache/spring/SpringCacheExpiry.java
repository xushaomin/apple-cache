package com.appleframework.cache.ehcache.spring;

import java.io.Serializable;
import java.time.Duration;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;


public class SpringCacheExpiry implements ExpiryPolicy<String, Serializable> {
	
	private Duration duration = Duration.ZERO;
	
	@Override
	public Duration getExpiryForCreation(String key, Serializable value) {
		return duration;
	}

	@Override
	public Duration getExpiryForAccess(String key, Supplier<? extends Serializable> value) {
		return duration;
	}

	@Override
	public Duration getExpiryForUpdate(String key, Supplier<? extends Serializable> oldValue, Serializable newValue) {
		return duration;
	}

	public SpringCacheExpiry(int seconds) {
		this.duration = Duration.ofSeconds(seconds);
	}
	
	public SpringCacheExpiry() {
		this.duration = Duration.ofSeconds(Long.MAX_VALUE);
	}
	
}
