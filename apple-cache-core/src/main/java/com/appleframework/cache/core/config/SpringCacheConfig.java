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
	
	//add set method for spring
	public void setSpringCacheEnable(boolean isCacheEnable) {
		SpringCacheConfig.isCacheEnable = isCacheEnable;
	}

	public void setSpringCacheObject(boolean isCacheObject) {
		SpringCacheConfig.isCacheObject = isCacheObject;
	}

	public void setSpringCacheKeyPrefix(String cacheKeyPrefix) {
		SpringCacheConfig.cacheKeyPrefix = cacheKeyPrefix;
	}

}
