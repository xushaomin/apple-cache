package com.appleframework.cache.j2cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.redisson.core.RKeys;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.replicator.CommandReplicator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@SuppressWarnings("deprecation")
public class J2RedissonBucketCacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = Logger.getLogger(J2RedissonBucketCacheManager.class);

	private String name = "J2_CACHE_MANAGER";

	private RedissonClient redisson;

	private CacheManager ehcacheManager;
	
	private CommandReplicator commandReplicator;

	public void setCommandReplicator(CommandReplicator commandReplicator) {
		this.commandReplicator = commandReplicator;
	}

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public RBucket<Object> getRedisCache(String key) {
		return redisson.getBucket(key);
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
			try {
				RKeys keys = redisson.getKeys();
				for (String key : keys.getKeys()) {
					getRedisCache(key).delete();
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
			publish(Command.create(CommandType.CLEAR));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			Object value = null;
			Element element = getEhCache().get(key);
			if (null == element) {
				try {
					value = getRedisCache(key).get();
				} catch (Exception e) {}
				
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
				try {
					value = (T)getRedisCache(key).get();
				} catch (Exception e) {}
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
			getRedisCache(key).delete();
			publish(Command.create(CommandType.DELETE, key));
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getRedisCache(key).set(value);
				publish(Command.create(CommandType.PUT, key));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				getRedisCache(key).set(value, expireTime, TimeUnit.SECONDS);
				publish(Command.create(CommandType.PUT, key, expireTime));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	private void publish(Command command) {
		try {
			commandReplicator.replicate(command);
		} catch (Exception e) {
			logger.error(e.getMessage());
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
					Object value = getRedisCache(key);
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
		return redisson.loadBucketValues(keys);
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		return redisson.loadBucketValues(keys);
	}
	
}