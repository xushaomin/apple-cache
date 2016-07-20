package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.redisson.core.RMapCache;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.core.replicator.CommandProcesser;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CommandTopicProcesser implements CommandProcesser {
	
	protected final static Logger logger = Logger.getLogger(CommandTopicProcesser.class);
	
	private String name = "J2_CACHE_MANAGER";
	
	private String type = "bucket";
		
	private CacheManager ehcacheManager;
	
	private RedissonClient redisson;
	
	public <T> RMapCache<String, T> getMapCache() {
		return redisson.getMapCache(name);
	}
	
	public RBucket<Object> getBucketCache(String key) {
		return redisson.getBucket(key);
	}

	public void onProcess(Command command) {
		Object key = command.getKey();
		Cache cache = getEhCache();
		if (null != cache) {
			if (command.getType().equals(CommandType.PUT)) {
				Integer timeout = command.getTimeout();
				cache.remove(key);
				Object value = null;
				if(type.equals("bucket")) {
					value = getBucketCache(key.toString()).get();
				}
				else {
					value = getMapCache().get(key);
				}
				if(timeout == 0) {
					getEhCache().put(new Element(key, value));
				} else {
					getEhCache().put(new Element(key, value, timeout));
				}
			} else if (command.getType().equals(CommandType.DELETE)) {
				cache.remove(key);
			} else if (command.getType().equals(CommandType.CLEAR)) {
				cache.removeAll();
			} else {
				logger.error("ERROR OPERATE TYPE !!!");
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
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

	public void setType(String type) {
		this.type = type;
	}
	
}
