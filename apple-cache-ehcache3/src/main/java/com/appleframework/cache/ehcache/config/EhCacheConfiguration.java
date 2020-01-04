package com.appleframework.cache.ehcache.config;

import java.util.HashMap;
import java.util.Map;

import com.appleframework.cache.ehcache.config.EhCacheProperties;

public class EhCacheConfiguration {

	private static String directory = System.getProperty("user.home");

	private static Map<String, EhCacheProperties> properties = new HashMap<String, EhCacheProperties>();

	public static String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		EhCacheConfiguration.directory = directory;
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


}