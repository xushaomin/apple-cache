package com.appleframework.cache.j2cache.spring;

import java.util.Map;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.MessageListener;
import org.redisson.core.RTopic;

import com.appleframework.cache.j2cache.topic.OperateObject;
import com.appleframework.cache.j2cache.topic.OperateObject.OperateType;
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
	private RTopic<OperateObject> topic;
	
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
		topic.addListener(new MessageListener<OperateObject>() {
			
		    public void onMessage(String channel, OperateObject message) {
		    	
		    	Object key = message.getKey();
		    	Cache cache = getEhCache();
		    	
		    	if(null != cache) {
			    	
			    	if(message.getOperateType().equals(OperateType.PUT)) {
			    		cache.remove(key);
			    	}
			    	else if(message.getOperateType().equals(OperateType.DELETE)) {
			    		cache.remove(key);
			    	}
			    	else if(message.getOperateType().equals(OperateType.CLEAR)) {
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
			publish(key, OperateType.PUT);
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
		publish(null, OperateType.CLEAR);
	}

	public void delete(String key) {
		try {
			getRedisCache().remove(key);
			publish(key, OperateType.DELETE);
		} catch (Exception e) {
			logger.warn("删除 Cache 缓存错误", e);
		}
	}

	public int getExpire() {
		return expire;
	}
	
	private void publish(Object key, OperateType operateType) {        
		OperateObject object = new OperateObject();
		object.setKey(key);
		object.setOperateType(operateType);
		try {
			topic.publish(object);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}	
}
