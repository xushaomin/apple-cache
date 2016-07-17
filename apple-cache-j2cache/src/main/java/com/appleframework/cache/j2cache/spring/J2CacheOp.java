package com.appleframework.cache.j2cache.spring;

import java.util.Map;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.MessageListener;
import org.redisson.core.RTopic;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.Command.CommandType;
import com.appleframework.cache.j2cache.utils.Contants;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class J2CacheOp {

	private static Logger logger = Logger.getLogger(J2CacheOp.class);

	private String name;
	private int expire;
	private RedissonClient redisson;
	private CacheManager ehcacheManager;
	private RTopic<Command> topic;
	
	public Map<String, Object> getRedisCache() {
		return redisson.getMap(name);
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

	public J2CacheOp(String name, int expire, CacheManager ehcacheManager, RedissonClient redisson) {
		this.name = name;
		this.expire = expire;
		this.redisson = redisson;
		this.ehcacheManager = ehcacheManager;
		init();
	}
	
	public J2CacheOp(String name, CacheManager ehcacheManager, RedissonClient redisson) {
		this.name = name;
		this.expire = 0;
		this.redisson = redisson;
		this.ehcacheManager = ehcacheManager;
		init();
	}
	
	public void init() {
		topic = redisson.getTopic(Contants.TOPIC_PREFIX_KEY + name);
		topic.addListener(new MessageListener<Command>() {
			
		    public void onMessage(String channel, Command message) {
		    	
		    	Object key = message.getKey();
		    	Cache cache = getEhCache();
		    	
		    	if(null != cache) {
			    	
			    	if(message.getType().equals(CommandType.PUT)) {
			    		cache.remove(key);
			    	}
			    	else if(message.getType().equals(CommandType.DELETE)) {
			    		cache.remove(key);
			    	}
			    	else if(message.getType().equals(CommandType.CLEAR)) {
			    		cache.removeAll();
			    	}
			    	else {
			    		logger.error("ERROR OPERATE TYPE !!!");
			    	}
		    	}
		    }
		});
		
	}

	public Object get(String key) {
		Object value = null;
		try {
			Element element = getEhCache().get(key);
			if(null == element) {
				value = getRedisCache().get(key);
				if(null != value)
					getEhCache().put(new Element(key, value));
			}
			else {
				value = element.getObjectValue();
			}
		} catch (Exception e) {
			logger.warn("获取 Cache 缓存错误", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			getRedisCache().put(key, value);
			publish(key, CommandType.PUT);
		} catch (Exception e) {
			logger.warn("更新 Cache 缓存错误", e);
		}
	}

	public void clear() {
		try {
			getRedisCache().clear();
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		}
		publish(null, CommandType.CLEAR);
	}

	public void delete(String key) {
		try {
			getRedisCache().remove(key);
			publish(key, CommandType.DELETE);
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
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
			topic.publish(object);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}	
}
