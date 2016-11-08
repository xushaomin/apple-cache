package com.appleframework.cache.codis.spring;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.spring.CacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;

public class SpringCacheOperationHset implements CacheOperation {
	
	private static String PRE_KEY = "cache:spring:";
	
	private CodisResourcePool codisResourcePool;

	private String name;
	private int expireTime = 0;	
	
	public SpringCacheOperationHset(CodisResourcePool codisResourcePool, String name) {
		this.name = name;
		this.expireTime = 0;
		this.codisResourcePool = codisResourcePool;
	}
	
	public SpringCacheOperationHset(CodisResourcePool codisResourcePool, String name, int expireTime) {
		this.name = name;
		this.expireTime = expireTime;
		this.codisResourcePool = codisResourcePool;
	}
	
	private byte[] getNameKey() {
		return (PRE_KEY + name).getBytes();
	}
	
	public Object get(String key) {
		Object value = null;
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] cacheValue = jedis.hget(getNameKey(), key.getBytes());
			if (null != cacheValue) {
				CacheObject cache = (CacheObject) SerializeUtility.unserialize(cacheValue);
				if (null != cache) {
					if (cache.isExpired()) {
						this.resetCacheObject(key, cache);
					} else {
						value = cache.getObject();
					}
				}
			}
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] byteKey = getNameKey();			
			CacheObject cache = new CacheObjectImpl(value, getExpiredTime());
			jedis.hset(byteKey, key.getBytes(), SerializeUtility.serialize(cache));
			if(expireTime > 0)
				jedis.expire(byteKey, expireTime);
		}
	}
	
	private void resetCacheObject(String key, CacheObject cache) {
		cache.setExpiredTime(getExpiredTime());
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.hset(getNameKey(), key.getBytes(), SerializeUtility.serialize(cache));
		}

	}
	
	private long getExpiredTime() {
		long lastTime = 2592000000L;
		if (expireTime > 0) {
			lastTime = expireTime * 1000;
		}
		return System.currentTimeMillis() + lastTime;
	}

	public void clear() {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.del(getNameKey());
		}
	}

	public void delete(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.hdel(getNameKey(), key.getBytes());
		}
	}

	public int getExpireTime() {
		return expireTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CodisResourcePool getCodisResourcePool() {
		return codisResourcePool;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
	
}
