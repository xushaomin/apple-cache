package com.appleframework.cache.ehcache.factory;

public class ConfigurationFactoryBean {

	private static String name = "apple_cache";
	private static int heap = 10;
	private static int offheap = 100;
	private static int disk = 1000;
	private static boolean persistent = false;

	public static int getHeap() {
		return heap;
	}

	public void setHeap(int heap) {
		ConfigurationFactoryBean.heap = heap;
	}

	public static int getOffheap() {
		return offheap;
	}

	public void setOffheap(int offheap) {
		ConfigurationFactoryBean.offheap = offheap;
	}

	public static int getDisk() {
		return disk;
	}

	public static void setDisk(int disk) {
		ConfigurationFactoryBean.disk = disk;
	}

	public static boolean isPersistent() {
		return persistent;
	}

	public static void setPersistent(boolean persistent) {
		ConfigurationFactoryBean.persistent = persistent;
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		ConfigurationFactoryBean.name = name;
	}

}