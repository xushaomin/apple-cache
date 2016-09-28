package com.appleframework.cache.codis.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private byte[] genByteKey(String key) {
		return SerializeUtility.serialize(key);
	}
	
	private byte[] genByteName() {
		return SerializeUtility.serialize(name);
	}

	public Object get(String key) {
		Object value = null;
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] field = genByteKey(key);
			
			List<byte[]> list = jedis.hmget(genByteName(), field);
			if(list.size() > 0) {
				byte[] cacheValue = list.get(0);
				if(null != cacheValue) {
					value = SerializeUtility.unserialize(cacheValue);
				}
			}
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] byteName = genByteName();
			byte[] byteKey = genByteKey(key);
			byte[] byteValue = SerializeUtility.serialize(value);
			
			Map<byte[], byte[]> hash = new HashMap<>();
			hash.put(byteKey, byteValue);
			jedis.hmset(byteName, hash);
			if(expireTime > 0)
				jedis.expire(byteName, expireTime);
		}
	}

	public void clear() {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.del(genByteName());
		}
	}

	public void delete(String key) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.hdel(genByteName(), genByteKey(key));
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
