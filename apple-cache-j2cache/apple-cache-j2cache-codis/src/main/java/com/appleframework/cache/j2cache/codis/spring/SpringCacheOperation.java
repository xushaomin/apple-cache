package com.appleframework.cache.j2cache.codis.spring;

import java.util.Set;

import org.apache.log4j.Logger;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.config.CacheConfig;
import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.core.spring.CacheOperation;
import com.appleframework.cache.core.utils.SerializeUtility;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import redis.clients.jedis.Jedis;

public class SpringCacheOperation implements CacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperation.class);

	private String name;
	private int expire = 0;
	private CodisResourcePool codisResourcePool;
	private CacheManager ehcacheManager;
	private CommandReplicator commandReplicator;

	public SpringCacheOperation(CacheManager ehcacheManager, CodisResourcePool codisResourcePool, String name) {
		this.name = name;
		this.codisResourcePool = codisResourcePool;
		this.ehcacheManager = ehcacheManager;
	}
	
	public SpringCacheOperation(CacheManager ehcacheManager, CodisResourcePool codisResourcePool, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.codisResourcePool = codisResourcePool;
		this.ehcacheManager = ehcacheManager;
	}
	
	public SpringCacheOperation(CacheManager ehcacheManager, CodisResourcePool codisResourcePool, String name, int expire,
			CommandReplicator commandReplicator) {
		this.name = name;
		this.expire = expire;
		this.codisResourcePool = codisResourcePool;
		this.ehcacheManager = ehcacheManager;
		this.commandReplicator = commandReplicator;
	}
	
	public Cache getEhCache() {
		Cache cache = ehcacheManager.getCache(name);
		if(null == cache) {
			ehcacheManager.addCache(name);
			return ehcacheManager.getCache(name);
		}
		else {
			return cache;
		}
	}
	
	public Object getFromRedis(String key) {
		if(!CacheConfig.isCacheEnable())
			return null;
		Object object = null;
		try {
			try (Jedis jedis = codisResourcePool.getResource()) {
				byte[] cacheValue = jedis.hget(name.getBytes(), key.getBytes());
				if (null != cacheValue) {
					object = SerializeUtility.unserialize(cacheValue);
				}
			}
		} catch (Exception e) {
			logger.warn("Cache Error : ", e);
		}
		return object;
	}

	public Object get(String key) {
		if(!CacheConfig.isCacheEnable())
			return null;
		Object value = null;
		try {
			Element element = getEhCache().get(key);
			if(null == element) {
				value = getFromRedis(key);
				if(null != value)
					getEhCache().put(new Element(key, value));
			}
			else {
				value = element.getObjectValue();
			}
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null || !CacheConfig.isCacheEnable())
			return;
		try {
			try (Jedis jedis = codisResourcePool.getResource()) {
				jedis.hset(name.getBytes(), key.getBytes(), SerializeUtility.serialize(value));
			}
			publish(key, CommandType.PUT);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void clear() {
		try (Jedis jedis = codisResourcePool.getResource()) {
			Set<String> keys = jedis.keys("*");
			for (String key : keys) {
				jedis.del(key);
			}
		}
		publish(null, CommandType.CLEAR);
	}

	public void delete(String key) {
		try {
			try (Jedis jedis = codisResourcePool.getResource()) {
				jedis.del(key.getBytes());
			}
			publish(key, CommandType.DELETE);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public int getExpire() {
		return expire;
	}
	
	private void publish(Object key, CommandType commandType) {        
		Command object = new Command();
		object.setKey(key);
		object.setType(commandType);
		try {
			replicate(object);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}	
	private void replicate(Command command) {
		commandReplicator.replicate(command);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setCommandReplicator(CommandReplicator commandReplicator) {
		this.commandReplicator = commandReplicator;
	}
	
}
