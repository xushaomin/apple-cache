package com.appleframework.cache.caffeine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Expiry;

public class CacheExpiry implements Expiry<String, Serializable> {

	private static Map<String, Long> durationMap = new HashMap<String, Long>();

	private static Long MAX = Long.MAX_VALUE;

	public static void setExpiry(String key, int seconds) {
		durationMap.put(getKey(key), Long.valueOf(seconds));
	}

	private static String getKey(String key) {
		return key;
	}

	@Override
	public long expireAfterCreate(String key, Serializable value, long currentTime) {
		Long expireTime = durationMap.get(getKey(key));
		if (null == expireTime) {
			return MAX;
		}
		return TimeUnit.SECONDS.toNanos(expireTime);
	}

	@Override
	public long expireAfterUpdate(String key, Serializable value, long currentTime, long currentDuration) {
		Long expireTime = durationMap.get(getKey(key));
		if (null == expireTime) {
			return MAX;
		}
		return TimeUnit.SECONDS.toNanos(expireTime);
	}

	@Override
	public long expireAfterRead(String key, Serializable value, long currentTime, long currentDuration) {
		Long expireTime = durationMap.get(getKey(key));
		if (null == expireTime) {
			return MAX;
		}
		return TimeUnit.SECONDS.toNanos(expireTime);
	}

}
