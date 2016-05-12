package com.appleframework.cache.codis;

import java.util.Set;

import redis.clients.jedis.Jedis;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.CacheService;
import com.appleframework.cache.core.utils.SerializeUtility;

public class CodisCacheService implements CacheService {
	
	private CodisResourcePool codisResourcePool;
	
	public CodisResourcePool getCodisResourcePool() {
		return codisResourcePool;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void clear() throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			Set<String> keys = jedis.keys("*");
			for (String key : keys) {
				jedis.del(key);
			}
		}
	}

	public Object get(String key) throws CacheException {		
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.get(key.getBytes());
			CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
			if(cache.isExpired()) {
				throw new CacheException("is expired");
			}
	     	return cache.getObject();
		}
	}

	@Override
	public CacheObject getCache(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.get(key.getBytes());
			CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
	     	return cache;
		}
	}

	@Override
	public void put(String key, Object value) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			CacheObject cache = new CacheObjectImpl(value, -1);
			jedis.set(key.getBytes(), SerializeUtility.serialize(cache));
		}
	}

	@Override
	public void put(String key, Object value, int expired) {
		long expiredL = System.currentTimeMillis() + expired * 1000;
		try (Jedis jedis = codisResourcePool.getResource()) {
			CacheObject cache = new CacheObjectImpl(value, expiredL);
			jedis.set(key.getBytes(), SerializeUtility.serialize(cache));
			jedis.expire(key.getBytes(), expired);
		}
	}

	@Override
	public Object remove(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.get(key.getBytes());
			jedis.del(key.getBytes());
			CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
	     	return cache.getObject();
		}
	}

	@Override
	public CacheObject removeCache(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.get(key.getBytes());
			jedis.del(key.getBytes());
			CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
	     	return cache;
		}
	}	
}