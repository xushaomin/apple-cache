package com.appleframework.cache.config;

import java.util.Properties;

import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.config.core.event.ConfigListener;

public class CacheEventListener implements ConfigListener {

	private static String KEY_CACHE_ENABLE = "spring.cache.enable";

	private static String KEY_CACHE_OBJECT = "spring.cache.object";

	private static String KEY_CACHE_PREFIX = "spring.cache.prefix";

	@Override
	public void receiveConfigInfo(Properties props) {
		Object cacheEnable = props.get(KEY_CACHE_ENABLE);
		if (null != cacheEnable) {
			SpringCacheConfig.setCacheEnable(Boolean.valueOf(cacheEnable.toString()));
		}
		Object cacheObject = props.get(KEY_CACHE_OBJECT);
		if (null != cacheObject) {
			SpringCacheConfig.setCacheObject(Boolean.valueOf(cacheObject.toString()));
		}
		Object cachePrefix = props.get(KEY_CACHE_PREFIX);
		if (null != cachePrefix) {
			SpringCacheConfig.setCacheKeyPrefix(cachePrefix.toString());
		}
	}

}
