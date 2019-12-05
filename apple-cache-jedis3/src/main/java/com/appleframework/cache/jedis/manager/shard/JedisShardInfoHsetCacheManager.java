package com.appleframework.cache.jedis.manager.shard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.JedisShardInfoFactory;

import redis.clients.jedis.Jedis;

@SuppressWarnings({ "unchecked" })
public class JedisShardInfoHsetCacheManager implements CacheManager {

	private static Logger logger = LoggerFactory.getLogger(JedisShardInfoHsetCacheManager.class);
	
	private JedisShardInfoFactory connectionFactory;
		
	private String name = "AC:";
	
	public void setConnectionFactory(JedisShardInfoFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	private Jedis getJedis() {
		return connectionFactory.getJedisConnection();
	}
	
	public void clear() throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			jedis.del(name.getBytes());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			byte[] value = jedis.hget(name.getBytes(), key.getBytes());
			if(null != value) {
				CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
				if(cache.isExpired()) {
					this.remove(key);
					return null;
				}
				else
					return cache.getObject(); 
			}
			else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			byte[] value = jedis.hget(name.getBytes(), key.getBytes());
			if(null != value) {
				CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
				if(cache.isExpired()) {
					this.remove(key);
					return null;
				}
				else
					return (T)cache.getObject(); 
			}
			else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			return jedis.hdel(name.getBytes(), key.getBytes())>0;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	@Override
	public void expire(String key, int timeout) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			byte[] value = jedis.hget(name.getBytes(), key.getBytes());
			if(null != value) {
				CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
				cache.setExpiredSecond(timeout);
				jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(cache));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public void set(String key, Object obj) throws CacheException {
		Jedis jedis = this.getJedis();
		if (null != obj) {
			try {
				CacheObject cache = new CacheObjectImpl(obj, getExpiredTime(0));
				jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(cache));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		Jedis jedis = this.getJedis();
		if (null != obj) {
			try {
				CacheObject cache = new CacheObjectImpl(obj, getExpiredTime(expireTime));
				jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(cache));
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
		    
			byte[][] fields = new byte[keys.length][];
			for (int i = 0; i < keys.length; i++) {
				fields[i] = keys[i].getBytes();
			}
			List<byte[]> byteList = jedis.hmget(name.getBytes(), fields);
			for (int i = 0; i < byteList.size(); i++) {
				byte[] value = byteList.get(i);
				if(null != value) {
					CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
					if(cache.isExpired()) {
						list.add(null);
						this.remove(keys[i]);
					}
					else {
						list.add(cache.getObject());
					}
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
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		Jedis jedis = this.getJedis();
		try {
			List<T> list = new ArrayList<T>();
		    
			byte[][] fields = new byte[keys.length][];
			for (int i = 0; i < keys.length; i++) {
				fields[i] = keys[i].getBytes();
			}
			List<byte[]> byteList = jedis.hmget(name.getBytes(), fields);
			for (int i = 0; i < byteList.size(); i++) {
				byte[] value = byteList.get(i);
				if(null != value) {
					CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
					if(cache.isExpired()) {
						list.add(null);
						this.remove(keys[i]);
					}
					else {
						list.add((T)cache.getObject());
					}
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
		    
			byte[][] fields = new byte[keys.length][];
			for (int i = 0; i < keys.length; i++) {
				fields[i] = keys[i].getBytes();
			}
			List<byte[]> byteList = jedis.hmget(name.getBytes(), fields);
			for (int i = 0; i < byteList.size(); i++) {
				byte[] value = byteList.get(i);
				if(null != value) {
					CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
					if(cache.isExpired()) {
						this.remove(keys[i]);
					}
					else {
						map.put(keys[i], cache.getObject());
					}
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
		    
			byte[][] fields = new byte[keys.length][];
			for (int i = 0; i < keys.length; i++) {
				fields[i] = keys[i].getBytes();
			}
			List<byte[]> byteList = jedis.hmget(name.getBytes(), fields);
			for (int i = 0; i < byteList.size(); i++) {
				byte[] value = byteList.get(i);
				if(null != value) {
					CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
					if(cache.isExpired()) {
						this.remove(keys[i]);
					}
					else {
						map.put(keys[i], (T)cache.getObject());
					}
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
	
	private long getExpiredTime(int expireTime) {
		long lastTime = 2592000000L;
		if (expireTime > 0) {
			lastTime = expireTime * 1000;
		}
		return System.currentTimeMillis() + lastTime;
	}

}