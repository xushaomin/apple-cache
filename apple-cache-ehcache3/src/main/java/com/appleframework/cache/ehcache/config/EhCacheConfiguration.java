package com.appleframework.cache.ehcache.config;

import java.util.HashMap;
import java.util.Map;

import com.appleframework.cache.ehcache.config.EhCacheProperties;

public class EhCacheConfiguration {

	private static String filePath = System.getProperty("user.home");
	private static Boolean cacheEnable = true;
	private static Boolean cacheObject = false;
	private static Map<String, Integer> expireConfig;

	private static Map<String, EhCacheProperties> properties = new HashMap<String, EhCacheProperties>();

	public static String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		EhCacheConfiguration.filePath = filePath;
	}

	public static Boolean getCacheEnable() {
		return cacheEnable;
	}

	public void setCacheEnable(Boolean cacheEnable) {
		EhCacheConfiguration.cacheEnable = cacheEnable;
	}

	public static Boolean getCacheObject() {
		return cacheObject;
	}

	public void setCacheObject(Boolean cacheObject) {
		EhCacheConfiguration.cacheObject = cacheObject;
	}

	public static Map<String, EhCacheProperties> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, EhCacheProperties> properties) {
		EhCacheConfiguration.properties = properties;
	}

	public void setProperties(String key, EhCacheProperties property) {
		EhCacheConfiguration.properties.put(key, property);
	}

	public static Map<String, Integer> getExpireConfig() {
		return expireConfig;
	}

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		EhCacheConfiguration.expireConfig = expireConfig;
	}

}