package com.appleframework.cache.redis;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.redis.utils.SerializeUtility;


public class RedisCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(RedisCacheManager.class);
	
	private int serializeType = 1; //序列化方式 1=byte[] =2json

	private JedisPool jedisPool;

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	public void setSerializeType(int serializeType) {
		this.serializeType = serializeType;
	}

	@SuppressWarnings("deprecation")
	public void clear() throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.flushDB();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@SuppressWarnings("deprecation")
	public Object get(String key) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			if(serializeType == 1) {
				byte[] value = jedis.get(key.getBytes());
		     	return SerializeUtility.unserialize(value);
			}
			else if (serializeType == 2) {
				return jedis.get(key);
			}
			else {
				logger.error("serializeType is error");
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			if(serializeType == 1) {
				byte[] value = jedis.get(key.getBytes());
		     	return (T)SerializeUtility.unserialize(value);
			}
			else if (serializeType == 2) {
				String message = jedis.get(key);
				return JSON.parseObject(message, clazz);
			}
			else {
				logger.error("serializeType is error");
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@SuppressWarnings("deprecation")
	public boolean remove(String key) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			if(serializeType == 1) {
				return jedis.del(key.getBytes())>0;
			}
			else if (serializeType == 2) {
				return jedis.del(key)>0;
			}
			else {
				logger.error("serializeType is error");
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void set(String key, Object obj) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		if (null != obj) {
			try {
				if(serializeType == 1) {
					String o = jedis.set(key.getBytes(), SerializeUtility.serialize(obj));
					logger.info(o);
				}
				else if (serializeType == 2) {
					String o = jedis.set(key, JSON.toJSONString(obj));
					logger.info(o);
				}
				else {
					logger.error("serializeType is error");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		this.set(key, obj);
	}

}