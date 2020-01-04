package com.appleframework.cache.ehcache;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

public class EhCacheExpiry implements ExpiryPolicy<String, Serializable> {

	private static Map<String, Duration> durationMap = new HashMap<String, Duration>();

	public static void setExpiry(String key, int seconds) {
		Duration duration = Duration.ofSeconds(seconds);
		if(seconds <= 0) {
			duration = Duration.ofSeconds(Long.MAX_VALUE);
		}
		durationMap.put(getKey(key), duration);
	}

	private static String getKey(String key) {
		return key;
	}

	@Override
	public Duration getExpiryForCreation(String key, Serializable value) {
		return durationMap.get(getKey(key));
	}

	@Override
	public Duration getExpiryForAccess(String key, Supplier<? extends Serializable> value) {
		return durationMap.get(getKey(key));
	}

	@Override
	public Duration getExpiryForUpdate(String key, Supplier<? extends Serializable> oldValue, Serializable newValue) {
		return durationMap.get(getKey(key));
	}

}
