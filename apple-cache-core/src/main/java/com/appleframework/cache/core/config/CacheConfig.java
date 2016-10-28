package com.appleframework.cache.core.config;

public class CacheConfig {
	
	private static boolean isCacheObject = false;

	private static boolean isCacheEnable = true;

	public static boolean isCacheEnable() {
		return isCacheEnable;
	}

	public static void setCacheEnable(boolean isCacheEnable) {
		CacheConfig.isCacheEnable = isCacheEnable;
	}

	public static boolean isCacheObject() {
		return isCacheObject;
	}

	public static void setCacheObject(boolean isCacheObject) {
		CacheConfig.isCacheObject = isCacheObject;
	}

}
