package com.appleframework.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.utils.SerializeUtility;

@SuppressWarnings({ "unchecked", "deprecation" })
public class RedisCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(RedisCacheManager.class);
	
	private JedisPool jedisPool;

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	public void clear() throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			Set<byte[]> keys = jedis.keys("*".getBytes());
			for (byte[] key : keys) {
				jedis.del(key);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Object get(String key) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] value = jedis.get(key.getBytes());
			return SerializeUtility.unserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] value = jedis.get(key.getBytes());
		    return (T)SerializeUtility.unserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public boolean remove(String key) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.del(key.getBytes())>0;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return false;
	}

	public void set(String key, Object obj) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		if (null != obj) {
			try {
				String o = jedis.set(key.getBytes(), SerializeUtility.serialize(obj));
				logger.info(o);
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		if (null != obj) {
			try {
				jedis.set(key.getBytes(), SerializeUtility.serialize(obj));
				jedis.expire(key.getBytes(), expireTime);
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				jedisPool.returnResource(jedis);
			}
		}
	}

	//批量获取
	@Override
	public List<Object> get(List<String> keyList) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			List<Object> list = new ArrayList<Object>();
			for (String key : keyList) {
				byte[] value = jedis.get(key.getBytes());
				Object object = SerializeUtility.unserialize(value);
				list.add(object);
			}
		    return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public List<Object> get(String... keys) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			List<Object> list = new ArrayList<Object>();
			for (String key : keys) {
				byte[] value = jedis.get(key.getBytes());
				Object object = SerializeUtility.unserialize(value);
				list.add(object);
			}
		    return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public <T> List<T> get(Class<T> clazz, List<String> keyList) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<T>();
			for (String key : keyList) {
				byte[] value = jedis.get(key.getBytes());
				T object = (T)SerializeUtility.unserialize(value);
				list.add(object);
			}
		    return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public <T> List<T> get(Class<T> clazz, String... keys) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<T>();
			for (String key : keys) {
				byte[] value = jedis.get(key.getBytes());
				T object = (T)SerializeUtility.unserialize(value);
				list.add(object);
			}
		    return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

}