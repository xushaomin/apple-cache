package com.appleframework.cache.j2cache;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.MessageListener;
import org.redisson.core.RMapCache;
import org.redisson.core.RTopic;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.j2cache.topic.OperateObject;
import com.appleframework.cache.j2cache.utils.Contants;
import com.appleframework.cache.j2cache.topic.OperateObject.OperateType;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class J2CacheManager implements com.appleframework.cache.core.CacheManager {

	private static Logger logger = Logger.getLogger(J2CacheManager.class);
	
	private String name = "J2_CACHE_MANAGER";
	
	private RedissonClient redisson;

	private CacheManager ehcacheManager;
	
	private RTopic<OperateObject> topic;
	
	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;		
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
	
	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public RMapCache<String, Object> getRedisCache() {
		return redisson.getMapCache(name);
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

	public void clear() throws CacheException {
		try {
			getRedisCache().clear();
			publish(null, OperateType.CLEAR);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			Object value = null;
			Element element = getEhCache().get(key);
			if(null == element) {
				value = getRedisCache().get(key);
				if(null != value)
					getEhCache().put(new Element(key, value));
			}
			else {
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
			if(null == element) {
				value = (T)getRedisCache().get(key);
				if(null != value)
					getEhCache().put(new Element(key, value));
			}
			else {
				value = (T)element.getObjectValue();
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
			publish(key, OperateType.DELETE);
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
				publish(key, OperateType.PUT);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				getRedisCache().put(key, value, expireTime, TimeUnit.SECONDS);
				publish(key, OperateType.PUT);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	private void publish(Object key, OperateType operateType) {        
		OperateObject object = new OperateObject();
		object.setKey(key);
		object.setOperateType(operateType);
		this.sendWithResson(object);
	}
	
	private void sendWithResson(OperateObject object) {
		try {
			topic.publish(object);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}