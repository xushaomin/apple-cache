package com.appleframework.cache.codis.spring;

import java.util.Set;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class SpringCacheOperationBucket implements CacheOperation {
		
	private CodisResourcePool codisResourcePool;

	private String name;
	private int expireTime = 0;
	
	public SpringCacheOperationBucket(CodisResourcePool codisResourcePool, String name, int expireTime) {
		this.name = name;
		this.expireTime = expireTime;
		this.codisResourcePool = codisResourcePool;
	}
	
	private byte[] getCacheKey(String key) {
		return (SpringCacheConfig.getCacheKeyPrefix() + name + ":value:" + key).getBytes();
	}
	
	private byte[] getNameKey() {
		return (SpringCacheConfig.getCacheKeyPrefix() + name + ":keys").getBytes();
	}

	public Object get(String key) {
		Object value = null;
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] cacheKey = getCacheKey(key);
			byte[] cacheValue = jedis.get(cacheKey);
			if (null != cacheValue) {
				value = SerializeUtility.unserialize(cacheValue);
			}
			else {
				jedis.srem(getNameKey(), cacheKey);
			}
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] byteKey = getCacheKey(key);
			byte[] byteName = getNameKey();
			jedis.set(byteKey, SerializeUtility.serialize(value));
			jedis.sadd(byteName, byteKey);
			if (expireTime > 0) {
				jedis.expire(byteKey, expireTime);
			}
		}
	}

	public void clear() {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] nameKey = getNameKey();
			Set<byte[]> set = jedis.smembers(nameKey);
			Pipeline pipeline = jedis.pipelined();
			for (byte[] bs : set) {
				pipeline.del(bs);
			}
			pipeline.del(nameKey);
			pipeline.sync();
		}
	}

	public void delete(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] cacheKey = this.getCacheKey(key);
			jedis.del(cacheKey);
			jedis.srem(getNameKey(), cacheKey);
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
	
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
	
	public CodisResourcePool getCodisResourcePool() {
		return codisResourcePool;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}
	
}
