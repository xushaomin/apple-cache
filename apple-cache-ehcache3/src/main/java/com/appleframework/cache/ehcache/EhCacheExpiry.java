package com.appleframework.cache.ehcache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.ehcache.ValueSupplier;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expiry;

@SuppressWarnings("deprecation")
public class EhCacheExpiry implements Expiry<String, Serializable> {
	
	private static Map<String, Duration> durationMap = new HashMap<>();
	
	public static void setExpiry(String key, int seconds) {
		Duration duration = Duration.of(seconds, TimeUnit.SECONDS);
		durationMap.put(getKey(key), duration);
	}

	@Override
	public Duration getExpiryForCreation(String key, Serializable value) {
		return durationMap.get(getKey(key));
	}

	@Override
	public Duration getExpiryForAccess(String key, ValueSupplier<? extends Serializable> value) {
		return durationMap.get(getKey(key));
	}

	@Override
	public Duration getExpiryForUpdate(String key, ValueSupplier<? extends Serializable> oldValue, Serializable newValue) {
		return durationMap.get(getKey(key));
	}
	
	private static String getKey(String key) {
		return key;
	}
}
