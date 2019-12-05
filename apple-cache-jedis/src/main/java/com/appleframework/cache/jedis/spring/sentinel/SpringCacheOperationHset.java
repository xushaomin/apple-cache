package com.appleframework.cache.jedis.spring.sentinel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseCacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.JedisSentinelFactory;

import redis.clients.jedis.Jedis;

public class SpringCacheOperationHset implements BaseCacheOperation {

	private static Logger logger = LoggerFactory.getLogger(SpringCacheOperationHset.class);

	private String name;
	private int expireTime = 0;
	private JedisSentinelFactory connectionFactory;

	public SpringCacheOperationHset(String name, int expireTime, JedisSentinelFactory connectionFactory) {
		this.name = name;
		this.expireTime = expireTime;
		this.connectionFactory = connectionFactory;
	}
	
	private byte[] getNameKey() {
		return (SpringCacheConfig.getCacheKeyPrefix() + name).getBytes();
	}
	
	public Object get(String key) {
		Object object = null;
		Jedis jedis = connectionFactory.getJedisConnection();
		try {
			byte[] cacheValue = jedis.hget(getNameKey(), key.getBytes());
			if (null != cacheValue) {
				CacheObject cache = (CacheObject) SerializeUtility.unserialize(cacheValue);
				if (null != cache) {
					if (cache.isExpired()) {
						this.resetCacheObject(key, cache);
					} else {
						object = cache.getObject();
					}
				}
			}
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
		return object;
	}
	
	private void resetCacheObject(String key, CacheObject cache) {
		Jedis jedis = connectionFactory.getJedisConnection();
		try {
			cache.setExpiredTime(getExpiredTime());
			byte[] byteKey = getNameKey();
			byte[] byteValue = SerializeUtility.serialize(cache);
			jedis.hset(byteKey, key.getBytes(), byteValue);
			if(expireTime > 0)
				jedis.expire(byteKey, expireTime * 2);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}
	
	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		Jedis jedis = connectionFactory.getJedisConnection();
		try {
			Object cache = new CacheObjectImpl(value, getExpiredTime());
			byte[] byteKey = getNameKey();
			byte[] byteValue = SerializeUtility.serialize(cache);
			jedis.hset(byteKey, key.getBytes(), byteValue);
			if(expireTime > 0)
				jedis.expire(byteKey, expireTime * 2);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void clear() {
		Jedis jedis = connectionFactory.getJedisConnection();
		try {
			jedis.del(getNameKey());
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void delete(String key) {
		Jedis jedis = connectionFactory.getJedisConnection();
		try {
			jedis.hdel(getNameKey(), key.getBytes());
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public int getExpireTime() {
		return expireTime;
	}
	
	private long getExpiredTime() {
		long lastTime = 2592000000L;
		if (expireTime > 0) {
			lastTime = expireTime * 1000;
		}
		return System.currentTimeMillis() + lastTime;
	}
}
