package com.appleframework.cache.core.spring;

public interface BaseCacheOperation {	

	public Object get(String key);

	public void put(String key, Object value);

	public void clear();

	public void delete(String key);
	
}
