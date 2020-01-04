package com.appleframework.cache.ehcache.config;

public class EhCacheProperties {

	private int heap = 10;
	private int offheap = 100;
	private int disk = 1000;
	private boolean persistent = false;
	private int expiry = 0;
	
	private boolean springCache = true;
	private boolean cacheObject = false;
	private boolean cacheEnable = true;

	
	public int getHeap() {
		return heap;
	}

	public void setHeap(int heap) {
		this.heap = heap;
	}

	public int getOffheap() {
		return offheap;
	}

	public void setOffheap(int offheap) {
		this.offheap = offheap;
	}

	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public int getExpiry() {
		return expiry;
	}

	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}

	public boolean isSpringCache() {
		return springCache;
	}

	public void setSpringCache(boolean springCache) {
		this.springCache = springCache;
	}

	public boolean isCacheObject() {
		return cacheObject;
	}

	public void setCacheObject(boolean cacheObject) {
		this.cacheObject = cacheObject;
	}

	public boolean isCacheEnable() {
		return cacheEnable;
	}

	public void setCacheEnable(boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
	}
	
}
