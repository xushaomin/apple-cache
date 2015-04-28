package com.appleframework.cache.core;

/**
 * @author xusm
 */
public interface CacheManager {

	// 获取缓存
	public Object get(String key) throws CacheException;
	
	// 获取缓存
	public <T> T get(String key, Class<T> clazz) throws CacheException;

	// 清除指定的缓存
	public boolean remove(String key) throws CacheException;

	// 清除全部的缓存 
	public void clear() throws CacheException;

	// 写入缓存信息，如果缓存已经存在，更新缓存
	public void set(String key, Object obj) throws CacheException;

	// 写入缓存信息，并设置过期时间（单位：秒），如果缓存已经存在，更新缓存和过期时间
	public void set(String key, Object obj, int timeout) throws CacheException;

}