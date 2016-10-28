package com.appleframework.cache.jedis.spring;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SuppressWarnings("deprecation")
public class SpringCacheOperation implements CacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperation.class);

	private String name;
	private int expireTime = 0;
	private JedisPool jedisPool;
	
	private Jedis getResource() {
		return jedisPool.getResource();
	}

	public SpringCacheOperation(String name, int expireTime, JedisPool jedisPool) {
		this.name = name;
		this.expireTime = expireTime;
		this.jedisPool = jedisPool;
	}
	
	public SpringCacheOperation(String name, JedisPool jedisPool) {
		this.name = name;
		this.expireTime = 0;
		this.jedisPool = jedisPool;
	}
	
	public Object get(String key) {
		Object object = null;
		Jedis jedis = getResource();
		try {
			byte[] cacheValue = jedis.hget(name.getBytes(), key.getBytes());
			if (null != cacheValue) {
				if (CacheConfig.isCacheObject()) {
					CacheObject cache = (CacheObject) SerializeUtility.unserialize(cacheValue);
					if (null != cache) {
						if (cache.isExpired()) {
							this.resetCacheObject(key, cache);
						} else {
							object = cache.getObject();
						}
					}
				} else {
					object = SerializeUtility.unserialize(cacheValue);
				}
			}

		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return object;
	}
	
	private void resetCacheObject(String key, CacheObject cache) {
		Jedis jedis = getResource();
		try {
			cache.setExpiredTime(getExpiredTime());
			byte[] byteValue = SerializeUtility.serialize(cache);
			jedis.hset(name.getBytes(), key.getBytes(), byteValue);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	public void put(String key, Object value) {
		if (value == null)
			this.delete(key);
		Jedis jedis = getResource();
		try {
			Object cache = null;
			
			if(CacheConfig.isCacheObject()) {
				cache = new CacheObjectImpl(value, getExpiredTime());
			}
			else {
				cache = value;
			}
			byte[] byteValue = SerializeUtility.serialize(cache);
			byte[] byteKey = name.getBytes();
			
			jedis.hset(byteKey, key.getBytes(), byteValue);
			if(expireTime > 0 && !CacheConfig.isCacheObject())
				jedis.expire(byteKey, expireTime);
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void clear() {
		Jedis jedis = getResource();
		try {
			jedis.del(name.getBytes());
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void delete(String key) {
		Jedis jedis = getResource();
		try {
			jedis.hdel(name.getBytes(), key.getBytes());
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
