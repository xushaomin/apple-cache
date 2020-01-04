package com.appleframework.cache.ehcache.config;

public class EhCacheProperties {

	private int heap = 10;
	private int offheap = 100;
	private int disk = 1000;
	private boolean persistent = false;
	private long expiry = 0;
	
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

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}
	
}
