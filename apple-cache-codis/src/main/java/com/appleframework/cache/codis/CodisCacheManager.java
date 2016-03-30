package com.appleframework.cache.codis;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.utils.SerializeUtility;

public class CodisCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(CodisCacheManager.class);
	
	private CodisResourcePool codisResourcePool;
	
	public CodisResourcePool getCodisResourcePool() {
		return codisResourcePool;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void clear() throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.flushDB();
		}
	}

	public Object get(String key) throws CacheException {		
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.get(key.getBytes());
	     	return SerializeUtility.unserialize(value);
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.get(key.getBytes());
	     	return (T)SerializeUtility.unserialize(value);
		}
	}

	public boolean remove(String key) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			return jedis.del(key.getBytes())>0;
		}
	}

	public void set(String key, Object obj) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			String o = jedis.set(key.getBytes(), SerializeUtility.serialize(obj));
			logger.info(o);
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			String o = jedis.set(key.getBytes(), SerializeUtility.serialize(obj));
			jedis.expire(key.getBytes(), expireTime);
			logger.info(o);
		}
	}

}