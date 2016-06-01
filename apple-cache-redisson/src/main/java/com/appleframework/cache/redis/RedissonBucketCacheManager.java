package com.appleframework.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.redisson.core.RKeys;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

@SuppressWarnings("deprecation")
public class RedissonBucketCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(RedissonBucketCacheManager.class);
		
	private RedissonClient redisson;

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
	
	public RBucket<Object> getCache(String name) {
		return redisson.getBucket(name);
	}

	public void clear() throws CacheException {
		try {
			RKeys keys = redisson.getKeys();
			for (String key : keys.getKeys()) {
				getCache(key).delete();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return getCache(key).get();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return (T)getCache(key).get();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			return getCache(key).delete();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getCache(key).set(value);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				getCache(key).set(value, expireTime, TimeUnit.SECONDS);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	//批量获取
	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		try {
			return this.getList(keyList.toArray(new String[keyList.size()]));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		try {
			Map<String, Object> map = redisson.loadBucketValues(keys);
			List<Object> list = new ArrayList<Object>();
			for (String key : keys) {
				list.add(map.get(key));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		try {
			return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		try {
			Map<String, T> map = redisson.loadBucketValues(keys);
			List<T> list = new ArrayList<T>();
			for (String key : keys) {
				list.add(map.get(key));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return redisson.loadBucketValues(keyList);
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		return redisson.loadBucketValues(keys);
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return redisson.loadBucketValues(keyList);
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		return redisson.loadBucketValues(keys);
	}
		
}