package com.appleframework.cache.core;

import java.util.List;
import java.util.Map;

/**
 * @author xusm
 */
public interface CacheManager2 extends CacheManager {

	//通过通配符*来匹配获取缓存
	public List<Object> getLists(String wkey) throws CacheException;
	
	//批量获取缓存 返回List
	public <T> List<T> getLists(Class<T> clazz, String wkey) throws CacheException;
	
	//通过通配符*来匹配获取缓存 返回Map
	public Map<String, Object> getMaps(String wkey) throws CacheException;
	
	//通过通配符*来匹配获取缓存 返回Map
	public <T> Map<String, T> getMaps(Class<T> clazz, String wkey) throws CacheException;
	
}