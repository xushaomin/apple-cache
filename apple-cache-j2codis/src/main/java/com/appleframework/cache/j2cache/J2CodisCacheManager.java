package com.appleframework.cache.j2cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.core.utils.SerializeUtility;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import redis.clients.jedis.Jedis;

public class J2CodisCacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = Logger.getLogger(J2CodisCacheManager.class);

	private String name = "J2_CACHE_MANAGER";

	private CodisResourcePool codisResourcePool;

	private CacheManager ehcacheManager;
	
	private CommandReplicator commandReplicator;

	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
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
		try {
			try (Jedis jedis = codisResourcePool.getResource()) {
				Set<String> keys = jedis.keys("*");
				for (String key : keys) {
					jedis.del(key);
				}
			}
			getEhCache().removeAll();
			this.replicate(Command.create(CommandType.CLEAR));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			Object value = null;
			Element element = getEhCache().get(key);
			if (null == element) {
				try (Jedis jedis = codisResourcePool.getResource()) {
					byte[] rvalue = jedis.get(key.getBytes());
					value = SerializeUtility.unserialize(rvalue);
				}
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
				try (Jedis jedis = codisResourcePool.getResource()) {
					byte[] rvalue = jedis.get(key.getBytes());
					value = (T)SerializeUtility.unserialize(rvalue);
				}
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
		try {
			try (Jedis jedis = codisResourcePool.getResource()) {
				jedis.del(key.getBytes());
			}
			getEhCache().remove(key);
			this.replicate(Command.create(CommandType.DELETE, key));
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				try (Jedis jedis = codisResourcePool.getResource()) {
					String o = jedis.set(key.getBytes(), SerializeUtility.serialize(value));
					logger.info(o);
				}
				getEhCache().put(new Element(key, value));
				this.replicate(Command.create(CommandType.PUT, key));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				try (Jedis jedis = codisResourcePool.getResource()) {
					String o = jedis.set(key.getBytes(), SerializeUtility.serialize(value));
					jedis.expire(key.getBytes(), expireTime);
					logger.info(o);
				}
				getEhCache().put(new Element(key, value, expireTime, expireTime));
				this.replicate(Command.create(CommandType.PUT, key, expireTime));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	// 批量获取
	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		try {
			List<Object> returnList = new ArrayList<Object>();
			//List<String> redisCacheKeyList = new ArrayList<String>();
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
		return null;
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		return null;
	}
	
	private void replicate(Command command) {
		commandReplicator.replicate(command);
	}
	
}