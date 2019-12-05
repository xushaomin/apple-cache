package com.appleframework.cache.redis.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

@SuppressWarnings({ "unchecked" })
public class RedisBucketCacheManager implements CacheManager {

	private static Logger logger = LoggerFactory.getLogger(RedisBucketCacheManager.class);

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	private String name = "AC:";

	private String getKey(String key) {
		return name + key;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void clear() throws CacheException {
		try {
			Set<String> keys = redisTemplate.keys("*");
			redisTemplate.delete(keys);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return redisTemplate.opsForValue().get(getKey(key));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return (T) redisTemplate.opsForValue().get(getKey(key));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			return redisTemplate.delete(getKey(key));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public void expire(String key, int expireTime) throws CacheException {
		try {
			redisTemplate.expire(getKey(key), expireTime, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				redisTemplate.opsForValue().set(getKey(key), value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				redisTemplate.opsForValue().set(getKey(key), value, expireTime, TimeUnit.SECONDS);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		return redisTemplate.opsForValue().multiGet(keyList);
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		List<String> keyList = new ArrayList<String>(Arrays.asList(keys));
		try {
			return this.getList(keyList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		List<T> list = new ArrayList<T>();
		try {
			List<Object> returnList = redisTemplate.opsForValue().multiGet(keyList);
			for (Object object : returnList) {
				list.add((T) object);
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		List<String> keyList = new ArrayList<String>(Arrays.asList(keys));
		try {
			return this.getList(clazz, keyList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		List<Object> returnList = redisTemplate.opsForValue().multiGet(keyList);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		for (int i = 0; i < keyList.size(); i++) {
			returnMap.put(keyList.get(i), returnList.get(i));
		}
		return returnMap;
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		List<String> keyList = new ArrayList<String>(Arrays.asList(keys));
		try {
			return this.getMap(keyList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		List<Object> returnList = redisTemplate.opsForValue().multiGet(keyList);
		Map<String, T> returnMap = new HashMap<String, T>();
		for (int i = 0; i < keyList.size(); i++) {
			returnMap.put(keyList.get(i), (T) returnList.get(i));
		}
		return returnMap;
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		List<String> keyList = new ArrayList<String>(Arrays.asList(keys));
		try {
			return this.getMap(clazz, keyList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}