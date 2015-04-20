package com.appleframework.cache.memcache.spring;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.log4j.Logger;

public class MemCache {

	private static Logger log = Logger.getLogger(MemCache.class);

	private Set<String> keySet = new HashSet<String>();
	private final String name;
	private final int expire;
	private final MemcachedClient memcachedClient;

	public MemCache(String name, int expire, MemcachedClient memcachedClient) {
		this.name = name;
		this.expire = expire;
		this.memcachedClient = memcachedClient;
	}
	
	public MemCache(String name, MemcachedClient memcachedClient) {
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
			log.warn("»ñÈ¡ Memcached »º´æ³¬Ê±", e);
		} catch (InterruptedException e) {
			log.warn("»ñÈ¡ Memcached »º´æ±»ÖÐ¶Ï", e);
		} catch (MemcachedException e) {
			log.warn("»ñÈ¡ Memcached »º´æ´íÎó", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			key = this.getKey(key);
			memcachedClient.setWithNoReply(key, expire, value);
			keySet.add(key);
		} catch (InterruptedException e) {
			log.warn("¸üÐÂ Memcached »º´æ±»ÖÐ¶Ï", e);
		} catch (MemcachedException e) {
			log.warn("¸üÐÂ Memcached »º´æ´íÎó", e);
		}
	}

	public void clear() {
		for (String key : keySet) {
			try {
				memcachedClient.deleteWithNoReply(this.getKey(key));
			} catch (InterruptedException e) {
				log.warn("É¾³ý Memcached »º´æ±»ÖÐ¶Ï", e);
			} catch (MemcachedException e) {
				log.warn("É¾³ý Memcached »º´æ´íÎó", e);
			}
		}
	}

	public void delete(String key) {
		try {
			key = this.getKey(key);
			memcachedClient.deleteWithNoReply(key);
		} catch (InterruptedException e) {
			log.warn("É¾³ý Memcached »º´æ±»ÖÐ¶Ï", e);
		} catch (MemcachedException e) {
			log.warn("É¾³ý Memcached »º´æ´íÎó", e);
		}
	}

	private String getKey(String key) {
		return name + "_" + key;
	}
}
