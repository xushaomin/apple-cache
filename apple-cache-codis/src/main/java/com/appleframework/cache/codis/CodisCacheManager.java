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
		Jedis jedis = codisResourcePool.getResource();
		try {
			jedis.flushDB();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		Jedis jedis = codisResourcePool.getResource();
		try {
			byte[] value = jedis.get(key.getBytes());
	     	return SerializeUtility.unserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		Jedis jedis = codisResourcePool.getResource();
		try {
			byte[] value = jedis.get(key.getBytes());
	     	return (T)SerializeUtility.unserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		Jedis jedis = codisResourcePool.getResource();
		try {
			return jedis.del(key.getBytes())>0;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public void set(String key, Object obj) throws CacheException {
		Jedis jedis = codisResourcePool.getResource();
		if (null != obj) {
			try {
				String o = jedis.set(key.getBytes(), SerializeUtility.serialize(obj));
				logger.info(o);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		this.set(key, obj);
	}

}