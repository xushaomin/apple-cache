package com.appleframework.cache.j2cache.jedis.spring;

import java.util.Set;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.spring.BaseCacheOperation;
import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.PoolFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SuppressWarnings("deprecation")
public class SpringCacheOperation implements BaseCacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperation.class);

	private String name;
	private int expire = 0;
	private PoolFactory poolFactory;
	private CacheManager ehcacheManager;
	
	private CommandReplicator commandReplicator;
	
	public SpringCacheOperation(CacheManager ehcacheManager, PoolFactory poolFactory, String name) {
		this.name = name;
		this.poolFactory = poolFactory;
		this.ehcacheManager = ehcacheManager;
	}
	
	public SpringCacheOperation(CacheManager ehcacheManager, PoolFactory poolFactory, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.poolFactory = poolFactory;
		this.ehcacheManager = ehcacheManager;
	}
	
	public SpringCacheOperation(CacheManager ehcacheManager, PoolFactory poolFactory, String name, int expire,
			CommandReplicator commandReplicator) {
		this.name = name;
		this.expire = expire;
		this.poolFactory = poolFactory;
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

	public Object get(String key) {
		if(!SpringCacheConfig.isCacheEnable())
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
	
	private Object getFromRedis(String key) {
		JedisPool jedisPool = poolFactory.getReadPool();
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] value = jedis.get(key.getBytes());
			return SerializeUtility.unserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	

	public void put(String key, Object value) {
		if (value == null || !SpringCacheConfig.isCacheEnable())
			return;
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		if (null != value) {
			try {
				String o = jedis.set(key.getBytes(), SerializeUtility.serialize(value));
				logger.info(o);
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				jedisPool.returnResource(jedis);
			}
			publish(key, CommandType.PUT);
		}
	}

	public void clear() {
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		try {
			Set<byte[]> keys = jedis.keys("*".getBytes());
			for (byte[] key : keys) {
				jedis.del(key);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		publish(null, CommandType.CLEAR);
	}

	public void delete(String key) {
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(key.getBytes());
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
		publish(key, CommandType.DELETE);
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
}
