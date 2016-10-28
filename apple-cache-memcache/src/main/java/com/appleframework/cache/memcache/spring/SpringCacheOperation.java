package com.appleframework.cache.memcache.spring;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class SpringCacheOperation implements CacheOperation {

	private static Logger log = Logger.getLogger(SpringCacheOperation.class);

	private Set<String> keySet = new HashSet<String>();
	private String name;
	private int expire = 0;
	private MemcachedClient memcachedClient;

	public SpringCacheOperation(MemcachedClient memcachedClient, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.memcachedClient = memcachedClient;
	}
	
	public SpringCacheOperation(MemcachedClient memcachedClient, String name) {
		this.name = name;
		this.expire = 0;
		this.memcachedClient = memcachedClient;
	}

	public Object get(String key) {
		Object value = null;
		try {
			key = this.getKey(key);
			if(CacheConfig.isCacheObject()) {
				CacheObject cache = (CacheObject) memcachedClient.get(key);
				if (null != cache) {
					if (cache.isExpired()) {
						this.resetCacheObject(key, cache);
					} else {
						value = cache.getObject();
					}
				}
			}
			else {
				value = memcachedClient.get(key);
			}			
		} catch (TimeoutException e) {
			log.error("get cache error :", e);
		} catch (InterruptedException e) {
			log.error("get cache error :", e);
		} catch (MemcachedException e) {
			log.error("get cache error :", e);
		}
		return value;
	}
	
	private void resetCacheObject(String key, CacheObject cache) {
		try {
			cache.setExpiredTime(getExpiredTime());
			memcachedClient.setWithNoReply(key, 0, cache);
		} catch (Exception e) {
			log.error("cache error", e);
		}
	}

	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		try {
			key = this.getKey(key);
			if(CacheConfig.isCacheObject()) {
				CacheObject object = new CacheObjectImpl(value, getExpiredTime());
				memcachedClient.setWithNoReply(key, 0, object);
			}
			else {
				if(expire > 0)
					memcachedClient.setWithNoReply(key, expire, value);
				else
					memcachedClient.setWithNoReply(key, 0, value);
			}
			keySet.add(key);
		} catch (InterruptedException e) {
			log.error("set cache error :", e);
		} catch (MemcachedException e) {
			log.error("set cache error :", e);
		}
	}

	public void clear() {
		for (String key : keySet) {
			try {
				memcachedClient.deleteWithNoReply(this.getKey(key));
			} catch (InterruptedException e) {
				log.error("clear cache error :", e);
			} catch (MemcachedException e) {
				log.error("clear cache error :", e);
			}
		}
	}

	public void delete(String key) {
		try {
			key = this.getKey(key);
			memcachedClient.deleteWithNoReply(key);
		} catch (InterruptedException e) {
			log.error("delete cache error :", e);
		} catch (MemcachedException e) {
			log.error("delete cache error :", e);
		}
	}

	private String getKey(String key) {
		return name + "_" + key;
	}
	
	private long getExpiredTime() {
		long lastTime = 2592000000L;
		if (expire > 0) {
			lastTime = expire * 1000;
		}
		return System.currentTimeMillis() + lastTime;
	}
}