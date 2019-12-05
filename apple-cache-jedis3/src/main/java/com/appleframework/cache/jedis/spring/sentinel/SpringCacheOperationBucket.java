package com.appleframework.cache.jedis.spring.sentinel;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseCacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.JedisSentinelFactory;

import redis.clients.jedis.Jedis;

public class SpringCacheOperationBucket implements BaseCacheOperation {

	private static Logger logger = LoggerFactory.getLogger(SpringCacheOperationBucket.class);

	private String name;
	private int expireTime = 0;
	private JedisSentinelFactory connectionFactory;

	public SpringCacheOperationBucket(String name, int expireTime, JedisSentinelFactory connectionFactory) {
		this.name = name;
		this.expireTime = expireTime;
		this.connectionFactory = connectionFactory;
	}
	
	private byte[] getCacheKey(String key) {
		return (SpringCacheConfig.getCacheKeyPrefix() + name + ":" + key).getBytes();
	}
	
	public Object get(String key) {
		Object value = null;
		Jedis jedis = connectionFactory.getJedisConnection();
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
		Jedis jedis = connectionFactory.getJedisConnection();
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
		Jedis jedis = connectionFactory.getJedisConnection();
		try {
			byte[] pattern = getCacheKey("*");
			Set<byte[]> set = jedis.hkeys(pattern);
			for (byte[] bs : set) {
				jedis.del(bs);
			}
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void delete(String key) {
		Jedis jedis = connectionFactory.getJedisConnection();
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
