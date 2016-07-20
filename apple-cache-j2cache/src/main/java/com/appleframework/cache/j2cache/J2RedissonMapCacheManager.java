package com.appleframework.cache.j2cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RMapCache;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.replicator.CommandReplicator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class J2RedissonMapCacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = Logger.getLogger(J2RedissonMapCacheManager.class);

	private String name = "J2_CACHE_MANAGER";

	private RedissonClient redisson;

	private CacheManager ehcacheManager;

	private CommandReplicator commandReplicator;
	
	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}

	public void setCommandReplicator(CommandReplicator commandReplicator) {
		this.commandReplicator = commandReplicator;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public <T> RMapCache<String, T> getRedisCache() {
		return redisson.getMapCache(name);
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
			getRedisCache().clear();
			publish(null, CommandType.CLEAR, 0);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			Object value = null;
			Element element = getEhCache().get(key);
			if (null == element) {
				value = getRedisCache().get(key);
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
				value = (T) getRedisCache().get(key);
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
			getRedisCache().remove(key);
			publish(key, CommandType.DELETE, 0);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getRedisCache().put(key, value);
				publish(key, CommandType.PUT, 0);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				getRedisCache().put(key, value, expireTime, TimeUnit.SECONDS);
				publish(key, CommandType.PUT, expireTime);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	private void publish(Object key, CommandType commandType, Integer timeout) {
		try {
			Command command = new Command();
			command.setKey(key);
			command.setType(commandType);
			command.setTimeout(timeout);
			commandReplicator.replicate(command);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	// 批量获取
	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		List<Object> list = new ArrayList<Object>();
		for (String key : keyList) {
			list.add(this.get(key));
		}
		return list;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}
}