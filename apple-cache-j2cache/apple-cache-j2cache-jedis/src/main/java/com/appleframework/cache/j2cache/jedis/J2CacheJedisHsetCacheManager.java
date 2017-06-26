package com.appleframework.cache.j2cache.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.PoolFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SuppressWarnings("deprecation")
public class J2CacheJedisHsetCacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = Logger.getLogger(J2CacheJedisHsetCacheManager.class);

	private String name = "AC_";

	private PoolFactory poolFactory;

	private CacheManager ehcacheManager;
	
	private CommandReplicator commandReplicator;

	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setPoolFactory(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	public void setCommandReplicator(CommandReplicator commandReplicator) {
		this.commandReplicator = commandReplicator;
	}

	public Cache getEhCache() {
		Cache cache = ehcacheManager.getCache(name);
		if (null == cache) {
			ehcacheManager.addCache(name);
			return ehcacheManager.getCache(name);
		} else {
			return cache;
		}
	}

	public void clear() throws CacheException {
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(name.getBytes());
			getEhCache().removeAll();
			this.replicate(Command.create(CommandType.CLEAR));
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	
	public Object getFromCache(String key) throws CacheException {
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
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
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Object get(String key) throws CacheException {
		try {
			Object value = null;
			Element element = getEhCache().get(key);
			if (null == element) {
				value = this.getFromCache(key);
				if (null != value)
					getEhCache().put(new Element(key, value));
			} else {
				value = element.getObjectValue();
			}
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}

	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			T value = null;
			Element element = getEhCache().get(key);
			if (null == element) {
				value = (T)this.getFromCache(key);
				if (null != value)
					getEhCache().put(new Element(key, value));
			} else {
				value = (T) element.getObjectValue();
			}
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(name.getBytes(), key.getBytes());
			getEhCache().remove(key);
			this.replicate(Command.create(CommandType.DELETE, key));
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return false;
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			JedisPool jedisPool = poolFactory.getReadPool();
			Jedis jedis = jedisPool.getResource();
			try {
				jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(value));
				getEhCache().put(new Element(key, value));
				this.replicate(Command.create(CommandType.PUT, key));
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			JedisPool jedisPool = poolFactory.getReadPool();
			Jedis jedis = jedisPool.getResource();
			try {
				jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(value));
				jedis.expire(key.getBytes(), expireTime);
				getEhCache().put(new Element(key, value, expireTime, expireTime));
				this.replicate(Command.create(CommandType.PUT, key, expireTime));
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		try {
			List<Object> returnList = new ArrayList<Object>();
			Map<Object, Element> ehcacheMap = getEhCache().getAll(keyList);
			for (int i = 0; i < keyList.size(); i++) {
				String key = keyList.get(i);
				Element element = ehcacheMap.get(key);
				if(null == element) {
					Object value = this.get(key);
					if (null != value) {
						getEhCache().put(new Element(key, value));
						returnList.set(i, value);
					}
				}
				else {
					returnList.set(i, element.getObjectValue());
				}
			}
			return returnList;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		List<Object> list = new ArrayList<Object>();
		for (String key : keys) {
			list.add(this.get(key));
		}
		return list;
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		List<T> list = new ArrayList<T>();
		for (String key : keyList) {
			list.add(this.get(key, clazz));
		}
		return list;
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		List<T> list = new ArrayList<T>();
		for (String key : keys) {
			list.add(this.get(key, clazz));
		}
		return list;
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		Map<String, Object> map = new HashMap<>();
		for (String key : keys) {
			map.put(key, this.get(key));
		}
		return map;
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		Map<String, T> map = new HashMap<>();
		for (String key : keys) {
			map.put(key, this.get(key, clazz));
		}
		return map;
	}
	
	private void replicate(Command command) {
		commandReplicator.replicate(command);
	}
	
}