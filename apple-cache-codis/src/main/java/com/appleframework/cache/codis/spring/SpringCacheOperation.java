package com.appleframework.cache.codis.spring;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;

public class SpringCacheOperation implements CacheOperation {
	
	private CodisResourcePool codisResourcePool;

	private String name;
	private int expireTime = 0;	
	
	public SpringCacheOperation(CodisResourcePool codisResourcePool, String name) {
		this.name = name;
		this.expireTime = 0;
		this.codisResourcePool = codisResourcePool;
	}
	
	public SpringCacheOperation(CodisResourcePool codisResourcePool, String name, int expireTime) {
		this.name = name;
		this.expireTime = expireTime;
		this.codisResourcePool = codisResourcePool;
	}

	public Object get(String key) {
		Object value = null;
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] cacheValue = jedis.hget(name.getBytes(), key.getBytes());
			if (null != cacheValue) {
				if (CacheConfig.isCacheObject()) {
					CacheObject cache = (CacheObject) SerializeUtility.unserialize(cacheValue);
					if (null != cache) {
						if (cache.isExpired()) {
							this.resetCacheObject(key, cache);
						} else {
							value = cache.getObject();
						}
					}
				} else {
					value = SerializeUtility.unserialize(cacheValue);
				}
			}
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		Object cache = null;
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] byteKey = name.getBytes();
			
			if(CacheConfig.isCacheObject()) {
				cache = new CacheObjectImpl(value, getExpiredTime());
			}
			else {
				cache = value;
			}
			jedis.hset(byteKey, key.getBytes(), SerializeUtility.serialize(cache));
			if(expireTime > 0 && !CacheConfig.isCacheObject())
				jedis.expire(byteKey, expireTime);
		}
	}
	
	private void resetCacheObject(String key, CacheObject cache) {
		cache.setExpiredTime(getExpiredTime());
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(cache));
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
			jedis.del(name.getBytes());
		}
	}

	public void delete(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.hdel(name.getBytes(), key.getBytes());
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
