package com.appleframework.cache.ehcache3.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.appleframework.cache.ehcache.config.EhCacheProperties;

@ConfigurationProperties(prefix = "apple.cache.ehcache3")
public class AppleCacheProperties {

	public static final String PREFIX = "apple.cache.ehcache3";

	private String directory = System.getProperty("user.home");
	
	private String initName = "default";

	private Boolean cacheEnable = true;

	private Boolean cacheObject = true;

	private Boolean springCache = false;

	private Map<String, EhCacheProperties> cacheTemplate;
		
	public String getInitName() {
		return initName;
	}

	public void setInitName(String initName) {
		this.initName = initName;
	}

	public Boolean getCacheEnable() {
		return cacheEnable;
	}

	public void setCacheEnable(Boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
	}

	public Boolean getCacheObject() {
		return cacheObject;
	}

	public void setCacheObject(Boolean cacheObject) {
		this.cacheObject = cacheObject;
	}

	public Boolean getSpringCache() {
		return springCache;
	}

	public void setSpringCache(Boolean springCache) {
		this.springCache = springCache;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Map<String, EhCacheProperties> getCacheTemplate() {
		return cacheTemplate;
	}

	public void setCacheTemplate(Map<String, EhCacheProperties> cacheTemplate) {
		this.cacheTemplate = cacheTemplate;
	}

}
