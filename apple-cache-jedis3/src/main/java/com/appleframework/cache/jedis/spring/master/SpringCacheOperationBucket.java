package com.appleframework.cache.jedis.spring.master;

import java.util.Set;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseCacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.PoolFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class SpringCacheOperationBucket implements BaseCacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperationBucket.class);

	private String name;
	private int expireTime = 0;
	private PoolFactory poolFactory;

	public SpringCacheOperationBucket(String name, int expireTime, PoolFactory poolFactory) {
		this.name = name;
		this.expireTime = expireTime;
		this.poolFactory = poolFactory;
	}
	
	private byte[] getCacheKey(String key) {
		return (SpringCacheConfig.getCacheKeyPrefix() + name + ":" + key).getBytes();
	}
	
	public Object get(String key) {
		Object value = null;
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] cacheValue = jedis.get(getCacheKey(key));
			if (null != cacheValue) {
				value = SerializeUtility.unserialize(cacheValue);
			}
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
		return value;
	}
	
	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] byteKey = getCacheKey(key);
			jedis.set(byteKey, SerializeUtility.serialize(value));
			if (expireTime > 0) {
				jedis.expire(byteKey, expireTime);
			}
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void clear() {
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] pattern = getCacheKey("*");
			Set<byte[]> set = jedis.keys(pattern);
			Pipeline pipeline = jedis.pipelined();
			for (byte[] bs : set) {
				pipeline.del(bs);
			}
			pipeline.sync();
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void delete(String key) {
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] cacheKey = this.getCacheKey(key);
			jedis.del(cacheKey);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public int getExpireTime() {
		return expireTime;
	}
	
}
