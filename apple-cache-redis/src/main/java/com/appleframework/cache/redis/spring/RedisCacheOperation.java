package com.appleframework.cache.redis.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCacheOperation {

	private static Logger logger = Logger.getLogger(RedisCacheOperation.class);

	private boolean isOpen = true;
	private String name;
	private int expireTime = 0;
	private JedisPool jedisPool;
	
	private Jedis getResource(String name) {
		return jedisPool.getResource();
	}

	public RedisCacheOperation(String name, int expireTime, JedisPool jedisPool) {
		this.name = name;
		this.expireTime = expireTime;
		this.jedisPool = jedisPool;
	}
	
	public RedisCacheOperation(String name, JedisPool jedisPool) {
		this.name = name;
		this.expireTime = 0;
		this.jedisPool = jedisPool;
	}

	private byte[] genByteKey(String key) {
		return SerializeUtility.serialize(key);
	}
	
	private byte[] genByteName() {
		return SerializeUtility.serialize(name);
	}
	
	@SuppressWarnings("deprecation")
	public Object get(String key) {
		if(!isOpen)
			return null;
		Object object = null;
		Jedis jedis = getResource(name);
		try {
			byte[] field = genByteKey(key);
			
			List<byte[]> list = jedis.hmget(genByteName(), field);
			if(list.size() > 0) {
				byte[] cacheValue = list.get(0);
				if(null != cacheValue) {
					if(CacheConfig.isCacheObject) {
						CacheObject cache = (CacheObject) SerializeUtility.unserialize(cacheValue);
						if (null != cache) {
							if (cache.isExpired()) {
								this.resetCacheObject(key, cache);
							} else {
								object = cache.getObject();
							}
						}
					}
					else {
						object = SerializeUtility.unserialize(cacheValue);
					}
				}
			}
		} catch (Exception e) {
			logger.warn("获取 Cache 缓存错误", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return object;
	}
	
	@SuppressWarnings({ "deprecation" })
	private void resetCacheObject(String key, CacheObject cache) {
		Jedis jedis = getResource(name);
		try {
			cache.setExpiredTime(getExpiredTime());
			
			byte[] byteName = genByteName();
			byte[] byteKey = genByteKey(key);
			byte[] byteValue = SerializeUtility.serialize(cache);
			
			Map<byte[], byte[]> hash = new HashMap<>();
			hash.put(byteKey, byteValue);
			jedis.hmset(byteName, hash);
		} catch (Exception e) {
			logger.warn("更新 Cache 缓存错误", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	@SuppressWarnings({ "deprecation" })
	public void put(String key, Object value) {
		if (value == null || !isOpen)
			return;
		Jedis jedis = getResource(name);
		try {
			Object cache = null;
			byte[] byteName = genByteName();
			byte[] byteKey = genByteKey(key);
			
			if(CacheConfig.isCacheObject) {
				cache = new CacheObjectImpl(value, getExpiredTime());
			}
			else {
				cache = value;
			}
			byte[] byteValue = SerializeUtility.serialize(cache);
			
			Map<byte[], byte[]> hash = new HashMap<>();
			hash.put(byteKey, byteValue);
			jedis.hmset(byteName, hash);
			if(expireTime > 0 && !CacheConfig.isCacheObject)
				jedis.expire(byteName, expireTime);
		} catch (Exception e) {
			logger.warn("更新 Cache 缓存错误", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@SuppressWarnings("deprecation")
	public void clear() {
		Jedis jedis = getResource(name);
		try {
			jedis.del(genByteName());
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void delete(String key) {
		Jedis jedis = getResource(name);
		try {
			jedis.hdel(genByteName(), genByteKey(key));
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
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
