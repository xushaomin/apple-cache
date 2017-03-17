package com.appleframework.cache.ehcache.spring;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.ehcache.ValueSupplier;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expiry;

public class SpringCacheExpiry implements Expiry<String, Serializable> {
	
	private Duration duration;

	@Override
	public Duration getExpiryForCreation(String key, Serializable value) {
		return duration;
	}

	@Override
	public Duration getExpiryForAccess(String key, ValueSupplier<? extends Serializable> value) {
		return null;
	}

	@Override
	public Duration getExpiryForUpdate(String key, ValueSupplier<? extends Serializable> oldValue, Serializable newValue) {
		return null;
	}
	
	public SpringCacheExpiry(int seconds) {
		this.duration = Duration.of(seconds, TimeUnit.SECONDS);
	}
	
	public SpringCacheExpiry() {
		this.duration = null;
	}
	
}
