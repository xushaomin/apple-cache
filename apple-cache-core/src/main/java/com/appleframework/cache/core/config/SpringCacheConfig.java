package com.appleframework.cache.core.config;

public class SpringCacheConfig {
	
	private static boolean isCacheObject = false;

	private static boolean isCacheEnable = true;
	
	private static String cacheKeyPrefix = "cache:spring";

	public static boolean isCacheEnable() {
		return isCacheEnable;
	}

	public static void setCacheEnable(boolean isCacheEnable) {
		SpringCacheConfig.isCacheEnable = isCacheEnable;
	}

	public static boolean isCacheObject() {
		return isCacheObject;
	}

	public static void setCacheObject(boolean isCacheObject) {
		SpringCacheConfig.isCacheObject = isCacheObject;
	}

	public static String getCacheKeyPrefix() {
		return cacheKeyPrefix;
	}

	public static void setCacheKeyPrefix(String cacheKeyPrefix) {
		SpringCacheConfig.cacheKeyPrefix = cacheKeyPrefix;
	}

}
