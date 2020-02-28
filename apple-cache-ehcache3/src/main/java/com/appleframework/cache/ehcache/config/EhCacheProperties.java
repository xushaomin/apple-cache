package com.appleframework.cache.ehcache.config;

public class EhCacheProperties {

	private int heap = 10;
	private int offheap = 100;
	private int disk = 1000;
	private boolean persistent = false;
	private int ttl = 0;
	private int tti = 0;
	
	private boolean springCache = true;
	private boolean cacheObject = false;
	
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

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getTti() {
		return tti;
	}

	public void setTti(int tti) {
		this.tti = tti;
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
	
}
