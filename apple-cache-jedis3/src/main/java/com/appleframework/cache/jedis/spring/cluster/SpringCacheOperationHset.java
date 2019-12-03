package com.appleframework.cache.jedis.spring.cluster;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.BaseCacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.JedisClusterFactory;

import redis.clients.jedis.JedisCluster;

public class SpringCacheOperationHset implements BaseCacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperationHset.class);

	private String name;
	private int expireTime = 0;
	private JedisClusterFactory connectionFactory;

	public SpringCacheOperationHset(String name, int expireTime, JedisClusterFactory connectionFactory) {
		this.name = name;
		this.expireTime = expireTime;
		this.connectionFactory = connectionFactory;
	}
	
	private byte[] getNameKey() {
		return (SpringCacheConfig.getCacheKeyPrefix() + name).getBytes();
	}
	
	public Object get(String key) {
		Object object = null;
		JedisCluster jedis = connectionFactory.getClusterConnection();
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
		JedisCluster jedis = connectionFactory.getClusterConnection();
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
		JedisCluster jedis = connectionFactory.getClusterConnection();
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
		JedisCluster jedis = connectionFactory.getClusterConnection();
		try {
			jedis.del(getNameKey());
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
	}

	public void delete(String key) {
		JedisCluster jedis = connectionFactory.getClusterConnection();
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
