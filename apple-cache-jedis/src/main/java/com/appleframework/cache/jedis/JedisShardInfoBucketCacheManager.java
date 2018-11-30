package com.appleframework.cache.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.JedisShardInfoFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

@SuppressWarnings({ "unchecked" })
public class JedisShardInfoBucketCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(JedisShardInfoBucketCacheManager.class);
	
	private JedisShardInfoFactory connectionFactory;
	
	private String name = "AC:";
		
	private String getKey(String key) {
		return name + key;
	}
	
	public void setConnectionFactory(JedisShardInfoFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	protected Jedis getJedis() {
		return connectionFactory.getJedisConnection();
	}

	public void clear() throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			jedis.del("*".getBytes());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			byte[] value = jedis.get(getKey(key).getBytes());
			return SerializeUtility.unserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			byte[] value = jedis.get(getKey(key).getBytes());
		    return (T)SerializeUtility.unserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			return jedis.del(getKey(key).getBytes())>0;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public void expire(String key, int expireTime) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			jedis.expire(getKey(key).getBytes(), expireTime);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public void set(String key, Object obj) throws CacheException {
		Jedis jedis = this.getJedis();
		if (null != obj) {
			try {
				jedis.set(getKey(key).getBytes(), SerializeUtility.serialize(obj));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		Jedis jedis = this.getJedis();
		if (null != obj) {
			try {
				jedis.set(getKey(key).getBytes(), SerializeUtility.serialize(obj));
				jedis.expire(getKey(key).getBytes(), expireTime);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		return this.getList(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			List<Object> list = new ArrayList<Object>();
			for (String key : keys) {
				byte[] value = jedis.get(getKey(key).getBytes());
				Object object = SerializeUtility.unserialize(value);
				list.add(object);
			}
		    return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			List<T> list = new ArrayList<T>();
		    Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(keys.length);

		    Pipeline pipeline = jedis.pipelined();
			for (String key : keys) {
				responses.put(key, pipeline.get(getKey(key).getBytes()));
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
		}
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
		    Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(keys.length);

		    Pipeline pipeline = jedis.pipelined();
			for (String key : keys) {
				responses.put(key, pipeline.get(getKey(key).getBytes()));
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
		}
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			Map<String, T> map = new HashMap<String, T>();
		    Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(keys.length);

		    Pipeline pipeline = jedis.pipelined();
			for (String key : keys) {
				responses.put(key, pipeline.get(getKey(key).getBytes()));
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
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}