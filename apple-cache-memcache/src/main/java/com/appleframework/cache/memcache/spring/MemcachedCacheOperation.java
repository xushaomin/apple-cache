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
	private final String name;
	private final int expire;
	private final MemcachedClient memcachedClient;

	public MemcachedCacheOperation(String name, int expire, MemcachedClient memcachedClient) {
		this.name = name;
		this.expire = expire;
		this.memcachedClient = memcachedClient;
	}
	
	public MemcachedCacheOperation(String name, MemcachedClient memcachedClient) {
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
			log.warn("��ȡ Memcached ���泬ʱ", e);
		} catch (InterruptedException e) {
			log.warn("��ȡ Memcached ���汻�ж�", e);
		} catch (MemcachedException e) {
			log.warn("��ȡ Memcached �������", e);
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
			log.warn("���� Memcached ���汻�ж�", e);
		} catch (MemcachedException e) {
			log.warn("���� Memcached �������", e);
		}
	}

	public void clear() {
		for (String key : keySet) {
			try {
				memcachedClient.deleteWithNoReply(this.getKey(key));
			} catch (InterruptedException e) {
				log.warn("ɾ�� Memcached ���汻�ж�", e);
			} catch (MemcachedException e) {
				log.warn("ɾ�� Memcached �������", e);
			}
		}
	}

	public void delete(String key) {
		try {
			key = this.getKey(key);
			memcachedClient.deleteWithNoReply(key);
		} catch (InterruptedException e) {
			log.warn("ɾ�� Memcached ���汻�ж�", e);
		} catch (MemcachedException e) {
			log.warn("ɾ�� Memcached �������", e);
		}
	}

	private String getKey(String key) {
		return name + "_" + key;
	}
}