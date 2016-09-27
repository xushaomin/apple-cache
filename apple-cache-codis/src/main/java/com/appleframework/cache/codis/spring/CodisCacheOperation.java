package com.appleframework.cache.codis.spring;

import java.util.Set;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;

public class CodisCacheOperation {

	private String name;
	private int expireTime = 0;
	private CodisResourcePool codisResourcePool;

	public CodisCacheOperation(String name, int expireTime, CodisResourcePool codisResourcePool) {
		this.name = name;
		this.expireTime = expireTime;
		this.codisResourcePool = codisResourcePool;
	}
	
	public CodisCacheOperation(String name, CodisResourcePool codisResourcePool) {
		this.name = name;
		this.expireTime = 0;
		this.codisResourcePool = codisResourcePool;
	}

	public Object get(String key) {
		Object value = null;
		try (Jedis jedis = codisResourcePool.getResource()) {
			value = jedis.get(SerializeUtility.serialize(key));
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.set(SerializeUtility.serialize(key), SerializeUtility.serialize(value));
			if(expireTime > 0)
				jedis.expire(key.getBytes(), expireTime);
		}
	}

	public void clear() {
		try (Jedis jedis = codisResourcePool.getResource()) {
			Set<byte[]> keys = jedis.keys("*".getBytes());
			for (byte[] key : keys) {
				jedis.del(key);
			}
		}
	}

	public void delete(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.del(SerializeUtility.serialize(key));
		}
	}

	public int getExpireTime() {
		return expireTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CodisResourcePool getCodisResourcePool() {
		return codisResourcePool;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
	
}
