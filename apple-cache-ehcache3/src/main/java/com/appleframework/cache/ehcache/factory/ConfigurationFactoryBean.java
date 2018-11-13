package com.appleframework.cache.ehcache.factory;

public class ConfigurationFactoryBean {

	private static int heap = 10;
	private static int offheap = 100;
	private static int disk = 1000;
	private static int sizeOfMaxObjectGraph;
	private static int sizeOfMaxObjectSize;
	private static int defaultSizeOfMaxObjectSize;
	private static int defaultSizeOfMaxObjectGraph;
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

	public static int getSizeOfMaxObjectGraph() {
		return sizeOfMaxObjectGraph;
	}

	public void setSizeOfMaxObjectGraph(int sizeOfMaxObjectGraph) {
		ConfigurationFactoryBean.sizeOfMaxObjectGraph = sizeOfMaxObjectGraph;
	}

	public static int getSizeOfMaxObjectSize() {
		return sizeOfMaxObjectSize;
	}

	public void setSizeOfMaxObjectSize(int sizeOfMaxObjectSize) {
		ConfigurationFactoryBean.sizeOfMaxObjectSize = sizeOfMaxObjectSize;
	}

	public static int getDefaultSizeOfMaxObjectSize() {
		return defaultSizeOfMaxObjectSize;
	}

	public void setDefaultSizeOfMaxObjectSize(int defaultSizeOfMaxObjectSize) {
		ConfigurationFactoryBean.defaultSizeOfMaxObjectSize = defaultSizeOfMaxObjectSize;
	}

	public static int getDefaultSizeOfMaxObjectGraph() {
		return defaultSizeOfMaxObjectGraph;
	}

	public void setDefaultSizeOfMaxObjectGraph(int defaultSizeOfMaxObjectGraph) {
		ConfigurationFactoryBean.defaultSizeOfMaxObjectGraph = defaultSizeOfMaxObjectGraph;
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

}