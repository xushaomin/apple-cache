package com.appleframework.cache.ehcache.config;

import java.util.HashMap;
import java.util.Map;

public class EhCacheConfiguration {

	private static String directory = System.getProperty("user.home");

	private static Map<String, EhCacheProperties> propertiesMap = new HashMap<String, EhCacheProperties>();

	public static String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		EhCacheConfiguration.directory = directory;
	}

	public static Map<String, EhCacheProperties> getProperties() {
		return propertiesMap;
	}

	public void setPropertiesMap(Map<String, EhCacheProperties> properties) {
		EhCacheConfiguration.propertiesMap = properties;
	}

	public void setProperties(String key, EhCacheProperties properties) {
		EhCacheConfiguration.propertiesMap.put(key, properties);
	}
	
	public void setProperties(EhCacheProperties properties) {
		EhCacheConfiguration.propertiesMap.put(EhCacheContants.DEFAULT_NAME, properties);
	}

}