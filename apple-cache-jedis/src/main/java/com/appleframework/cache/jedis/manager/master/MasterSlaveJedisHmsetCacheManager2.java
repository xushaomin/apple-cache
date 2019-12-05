package com.appleframework.cache.jedis.manager.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager2;
import com.appleframework.cache.core.utils.ReflectionUtility;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

@SuppressWarnings({ "deprecation" })
public class MasterSlaveJedisHmsetCacheManager2 extends MasterSlaveJedisHmsetCacheManager implements CacheManager2 {

	private static Logger logger = LoggerFactory.getLogger(MasterSlaveJedisHmsetCacheManager2.class);

	private Map<String, Response<Map<byte[], byte[]>>> pipeline(Jedis jedis, String wkey) {
		Set<byte[]> keys = jedis.keys(wkey.getBytes());
		Map<String, Response<Map<byte[], byte[]>>> responses 
			= new HashMap<String, Response<Map<byte[], byte[]>>>(keys.size());

		Pipeline pipeline = jedis.pipelined();
		for (byte[] pkey : keys) {
			String ppkey = new String(pkey);
			responses.put(ppkey, pipeline.hgetAll(pkey));
		}
		pipeline.sync();
		return responses;
	}
	
	@Override
	public List<Object> getLists(String wkey) throws CacheException {
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
		try {
			List<Object> list = new ArrayList<Object>();
			Map<String, Response<Map<byte[], byte[]>>> responses = this.pipeline(jedis, wkey);
			for (String pkey : responses.keySet()) {
				Response<Map<byte[], byte[]>> response = responses.get(pkey);
				Map<byte[], byte[]> value = response.get();
				if (null != value) {
					Map<String, Object> object = new HashMap<>();
					for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
						String boKey = new String(entry.getKey());
						Object boValue = (Object) SerializeUtility.unserialize(entry.getValue());
						object.put(boKey, boValue);
					}
					list.add(object);
				} else {
					list.add(null);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public <T> List<T> getLists(Class<T> clazz, String wkey) throws CacheException {
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<T>();
			Map<String, Response<Map<byte[], byte[]>>> responses = this.pipeline(jedis, wkey);
			String[] stringFields = super.getStrProperties(clazz);
			for (String pkey : responses.keySet()) {
				Response<Map<byte[], byte[]>> response = responses.get(pkey);
				Map<byte[], byte[]> value = response.get();
				if (null != value) {
					T object = clazz.newInstance();
					for (int i = 0; i < stringFields.length; i++) {
						String boKey = stringFields[i];
						Object boValue = (Object) SerializeUtility.unserialize(value.get(boKey.getBytes()));
						if (null != boValue) {
							try {
								ReflectionUtility.invokeSetterMethod(object, boKey, boValue);
							} catch (Exception e) {
								logger.info(e.getMessage());
							}
						}
					}
					list.add(object);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return null;
	}

	@Override
	public Map<String, Object> getMaps(String wkey) throws CacheException {
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Response<Map<byte[], byte[]>>> responses = this.pipeline(jedis, wkey);
			for (String pkey : responses.keySet()) {
				Response<Map<byte[], byte[]>> response = responses.get(pkey);
				Map<byte[], byte[]> value = response.get();
				if (null != value) {
					Map<String, Object> object = new HashMap<>();
					for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
						String boKey = new String(entry.getKey());
						Object boValue = (Object) SerializeUtility.unserialize(entry.getValue());
						object.put(boKey, boValue);
					}
					map.put(pkey, object);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return null;
	}

	@Override
	public <T> Map<String, T> getMaps(Class<T> clazz, String wkey) throws CacheException {
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
		try {
			Map<String, T> map = new HashMap<String, T>();
			Map<String, Response<Map<byte[], byte[]>>> responses = this.pipeline(jedis, wkey);
			String[] stringFields = super.getStrProperties(clazz);
			for (String pkey : responses.keySet()) {
				Response<Map<byte[], byte[]>> response = responses.get(pkey);
				Map<byte[], byte[]> value = response.get();
				if (null != value) {
					T object = clazz.newInstance();
					for (int i = 0; i < stringFields.length; i++) {
						String boKey = stringFields[i];
						Object boValue = (Object) SerializeUtility.unserialize(value.get(boKey.getBytes()));
						if (null != boValue) {
							try {
								ReflectionUtility.invokeSetterMethod(object, boKey, boValue);
							} catch (Exception e) {
								logger.info(e.getMessage());
							}
						}
					}
					map.put(pkey, object);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return null;
	}

}