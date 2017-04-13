package com.appleframework.cache.codis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;

@SuppressWarnings("unchecked")
public class CodisHsetCacheManager implements CacheManager {

	private CodisResourcePool codisResourcePool;
	
	private String name = "AC_";
	
	public CodisResourcePool getCodisResourcePool() {
		return codisResourcePool;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}
		
	public void clear() throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			jedis.del(name.getBytes());
		}
	}

	public Object get(String key) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.hget(name.getBytes(), key.getBytes());
			if(null != value) {
				CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
				if(null == cache)
					return null;
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
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			byte[] value = jedis.hget(name.getBytes(), key.getBytes());
			if(null != value) {
				CacheObject cache = (CacheObject)SerializeUtility.unserialize(value);
				if(null == cache)
					return null;
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
		}
	}

	public boolean remove(String key) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			return jedis.hdel(name.getBytes(), key.getBytes())>0;
		}
	}

	public void set(String key, Object obj) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			if (null != obj) {
				CacheObject cache = new CacheObjectImpl(obj, getExpiredTime(0));
				jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(cache));
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
			CacheObject cache = new CacheObjectImpl(obj, getExpiredTime(expireTime));
			jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(cache));
		}
	}

	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		return this.getList(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
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
					if(null == cache)
						list.add(null);
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
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
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
					if(null == cache)
						list.add(null);
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
		}
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
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
					if(null == cache)
						this.remove(keys[i]);
					if(cache.isExpired()) {
						this.remove(keys[i]);
					}
					else {
						map.put(keys[i], cache.getObject());
					}
				}
			}
			return map;
		}
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		try (Jedis jedis = codisResourcePool.getResource()) {
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
					if(null == cache)
						this.remove(keys[i]);
					if(cache.isExpired()) {
						this.remove(keys[i]);
					}
					else {
						map.put(keys[i], (T)cache.getObject());
					}
				}
			}
			return map;
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