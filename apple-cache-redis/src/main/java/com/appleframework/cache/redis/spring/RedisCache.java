package com.appleframework.cache.redis.spring;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RMapCache;

public class RedisCache {

	private static Logger logger = Logger.getLogger(RedisCache.class);

	private final String name;
	private final int expire;
	private final RedissonClient redisson;
	
	public RMapCache<String, Object> getCacheMap() {
		return redisson.getMapCache(name);
	}

	public RedisCache(String name, int expire, RedissonClient redisson) {
		this.name = name;
		this.expire = expire;
		this.redisson = redisson;
	}
	
	public RedisCache(String name, RedissonClient redisson) {
		this.name = name;
		this.expire = 0;
		this.redisson = redisson;
	}

	public Object get(String key) {
		Object value = null;
		try {
			value = getCacheMap().get(key);
		} catch (Exception e) {
			logger.warn("获取 Cache 缓存错误", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			if(expire == 0)
				getCacheMap().put(key, value);
			else
				getCacheMap().put(key, value, expire, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.warn("更新 Cache 缓存错误", e);
		}
	}

	public void clear() {
		try {
			getCacheMap().clear();
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		}
	}

	public void delete(String key) {
		try {
			getCacheMap().remove(key);
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		}
	}

	public int getExpire() {
		return expire;
	}
	
}
