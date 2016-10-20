package com.appleframework.cache.memcache.spring;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.log4j.Logger;

public class MemcachedCacheOperation {

	private static Logger log = Logger.getLogger(MemcachedCacheOperation.class);

	private Set<String> keySet = new HashSet<String>();
	private String name;
	private int expire = 0;
	private MemcachedClient memcachedClient;

	public MemcachedCacheOperation(MemcachedClient memcachedClient, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.memcachedClient = memcachedClient;
	}
	
	public MemcachedCacheOperation(MemcachedClient memcachedClient, String name) {
		this.name = name;
		this.expire = 0;
		this.memcachedClient = memcachedClient;
	}

	public Object get(String key) {
		Object value = null;
		try {
			key = this.getKey(key);
			value = memcachedClient.get(key);
		} catch (TimeoutException e) {
			log.warn("get cache error :", e);
		} catch (InterruptedException e) {
			log.warn("get cache error :", e);
		} catch (MemcachedException e) {
			log.warn("get cache error :", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			key = this.getKey(key);
			if(expire > 0)
				memcachedClient.setWithNoReply(key, expire, value);
			else
				memcachedClient.setWithNoReply(key, 0, value);
			keySet.add(key);
		} catch (InterruptedException e) {
			log.warn("set cache error :", e);
		} catch (MemcachedException e) {
			log.warn("set cache error :", e);
		}
	}

	public void clear() {
		for (String key : keySet) {
			try {
				memcachedClient.deleteWithNoReply(this.getKey(key));
			} catch (InterruptedException e) {
				log.warn("clear cache error :", e);
			} catch (MemcachedException e) {
				log.warn("clear cache error :", e);
			}
		}
	}

	public void delete(String key) {
		try {
			key = this.getKey(key);
			memcachedClient.deleteWithNoReply(key);
		} catch (InterruptedException e) {
			log.warn("delete cache error :", e);
		} catch (MemcachedException e) {
			log.warn("delete cache error :", e);
		}
	}

	private String getKey(String key) {
		return name + "_" + key;
	}
}