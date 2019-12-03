package com.appleframework.cache.ehcache3.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apple.cache.ehcache3")
public class AppleCacheProperties {

	public static final String PREFIX = "apple.cache.ehcache3";

	private String name = "apple_cache";

	private Integer heap = 10;

	private Integer offheap = 100;

	private Integer disk = 1000;

	private Boolean persistent = false;

	private String filePath = System.getProperty("user.home");

	private Boolean cacheEnable = true;

	private Boolean cacheObject = true;

	private String cacheKeyPrefix;

	private Map<String, Integer> expireConfig;

	private Boolean springCache = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getHeap() {
		return heap;
	}

	public void setHeap(Integer heap) {
		this.heap = heap;
	}

	public Integer getOffheap() {
		return offheap;
	}

	public void setOffheap(Integer offheap) {
		this.offheap = offheap;
	}

	public Integer getDisk() {
		return disk;
	}

	public void setDisk(Integer disk) {
		this.disk = disk;
	}

	public Boolean getPersistent() {
		return persistent;
	}

	public void setPersistent(Boolean persistent) {
		this.persistent = persistent;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	public String getCacheKeyPrefix() {
		return cacheKeyPrefix;
	}

	public void setCacheKeyPrefix(String cacheKeyPrefix) {
		this.cacheKeyPrefix = cacheKeyPrefix;
	}

	public Map<String, Integer> getExpireConfig() {
		return expireConfig;
	}

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		this.expireConfig = expireConfig;
	}

	public Boolean getSpringCache() {
		return springCache;
	}

	public void setSpringCache(Boolean springCache) {
		this.springCache = springCache;
	}

}
