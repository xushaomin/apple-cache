package com.appleframework.cache.redis.spring;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.redisson.Redisson;

public class RedisCache {

	private static Logger log = Logger.getLogger(RedisCache.class);

	private Set<String> keySet = new HashSet<String>();
	private final String name;
	private final int expire;
	private final Redisson redisson;
	
	public Map<String, Object> getCacheMap() {
		return redisson.getMap(name);
	}

	public RedisCache(String name, int expire, Redisson redisson) {
		this.name = name;
		this.expire = expire;
		this.redisson = redisson;
	}
	
	public RedisCache(String name, Redisson redisson) {
		this.name = name;
		this.expire = 0;
		this.redisson = redisson;
	}

	public Object get(String key) {
		Object value = null;
		try {
			key = this.getKey(key);
			value = getCacheMap().get(key);
		} catch (Exception e) {
			log.warn("»ñÈ¡ Cache »º´æ´íÎó", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			key = this.getKey(key);
			getCacheMap().put(key, value);
			keySet.add(key);
		} catch (Exception e) {
			log.warn("¸üÐÂ Cache »º´æ´íÎó", e);
		}
	}

	public void clear() {
		for (String key : keySet) {
			try {
				getCacheMap().remove(this.getKey(key));
			} catch (Exception e) {
				log.warn("É¾³ý Cache »º´æ´íÎó", e);
			}
		}
	}

	public void delete(String key) {
		try {
			key = this.getKey(key);
			getCacheMap().remove(key);
		} catch (Exception e) {
			log.warn("É¾³ý Cache »º´æ´íÎó", e);
		}
	}

	private String getKey(String key) {
		return name + "_" + key;
	}

	public int getExpire() {
		return expire;
	}
	
}
