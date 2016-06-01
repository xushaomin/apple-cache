package com.appleframework.cache.core;

import java.util.List;
import java.util.Map;

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
	
	// 批量获取缓存 返回List
	public List<Object> getList(List<String> keyList) throws CacheException;
	
	public List<Object> getList(String... keys) throws CacheException;
	
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException;
	
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException;
	
	//批量获取缓存 返回Map
	public Map<String, Object> getMap(List<String> keyList) throws CacheException;
	
	public Map<String, Object> getMap(String... keys) throws CacheException;
	
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException;
	
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException;

}