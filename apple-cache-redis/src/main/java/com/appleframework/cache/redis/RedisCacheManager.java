package com.appleframework.cache.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

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
	public List<Object> getList(List<String> keyList) throws CacheException {
		return this.getList(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
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
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<T>();
		    Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(keys.length);

			Pipeline pipeline = jedis.pipelined();
			for (String key : keys) {
				responses.put(key, pipeline.get(key.getBytes()));
			}
			pipeline.sync();
			
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					list.add((T)SerializeUtility.unserialize(value));
				}
				else {
					list.add(null);
				}
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
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
		    Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(keys.length);

			Pipeline pipeline = jedis.pipelined();
			for (String key : keys) {
				responses.put(key, pipeline.get(key.getBytes()));
			}
			pipeline.sync();
			
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					map.put(key, SerializeUtility.unserialize(value));
				}
				else {
					map.put(key, null);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			Map<String, T> map = new HashMap<String, T>();
		    Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(keys.length);

			Pipeline pipeline = jedis.pipelined();
			for (String key : keys) {
				responses.put(key, pipeline.get(key.getBytes()));
			}
			pipeline.sync();
			
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					map.put(key, (T)SerializeUtility.unserialize(value));
				}
				else {
					map.put(key, null);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

}