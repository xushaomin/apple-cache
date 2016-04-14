package com.appleframework.cache.redis.spring;

import java.util.Set;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCache {

	private static Logger logger = Logger.getLogger(RedisCache.class);

	private String name;
	private int expireTime = 0;
	private JedisPool jedisPool;
	
	private Jedis getResource(String name) {
		return jedisPool.getResource();
	}

	public RedisCache(String name, int expireTime, JedisPool jedisPool) {
		this.name = name;
		this.expireTime = expireTime;
		this.jedisPool = jedisPool;
	}
	
	public RedisCache(String name, JedisPool jedisPool) {
		this.name = name;
		this.expireTime = 0;
		this.jedisPool = jedisPool;
	}

	@SuppressWarnings("deprecation")
	public Object get(String key) {
		Object value = null;
		Jedis jedis = getResource(name);
		try {
			value = jedis.get(SerializeUtility.serialize(key));
		} catch (Exception e) {
			logger.warn("获取 Cache 缓存错误", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	@SuppressWarnings({ "deprecation" })
	public void put(String key, Object value) {
		if (value == null)
			return;
		Jedis jedis = getResource(name);
		try {
			jedis.set(SerializeUtility.serialize(key), SerializeUtility.serialize(value));
			if(expireTime > 0)
				jedis.expire(key.getBytes(), expireTime);
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
			Set<byte[]> keys = jedis.keys("*".getBytes());
			for (byte[] key : keys) {
				jedis.del(key);
			}
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void delete(String key) {
		Jedis jedis = getResource(name);
		try {
			jedis.del(SerializeUtility.serialize(key));
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		}
	}

	public int getExpireTime() {
		return expireTime;
	}
	
}
