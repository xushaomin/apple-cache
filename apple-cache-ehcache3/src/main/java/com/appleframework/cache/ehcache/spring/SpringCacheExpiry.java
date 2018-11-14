package com.appleframework.cache.ehcache.spring;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.ehcache.ValueSupplier;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expiry;

@SuppressWarnings("deprecation")
public class SpringCacheExpiry implements Expiry<String, Serializable> {
	
	private Duration duration = Duration.ZERO;

	@Override
	public Duration getExpiryForCreation(String key, Serializable value) {
		return duration;
	}

	@Override
	public Duration getExpiryForAccess(String key, ValueSupplier<? extends Serializable> value) {
		return duration;
	}

	@Override
	public Duration getExpiryForUpdate(String key, ValueSupplier<? extends Serializable> oldValue, Serializable newValue) {
		return duration;
	}
	
	public SpringCacheExpiry(int seconds) {
		this.duration = Duration.of(seconds, TimeUnit.SECONDS);
	}
	
	public SpringCacheExpiry() {
		this.duration = null;
	}
	
}
