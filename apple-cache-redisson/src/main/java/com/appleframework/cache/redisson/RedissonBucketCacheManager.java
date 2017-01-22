package com.appleframework.cache.redisson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RFuture;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

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
	@SuppressWarnings("unchecked")
	public List<Object> getList(String... keys) throws CacheException {
		try {
			return (List<Object>) getBatchResponseList(keys);
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
			return getBatchResponseListT(clazz, keys);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getBatchResponseMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		return this.getBatchResponseMap(keys);
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getBatchResponseMapT(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		return this.getMap(clazz, keys);
	}
	
	private List<?> getBatchResponseList(String... keys) {
		RBatch batch = redisson.createBatch();
		for (String key : keys) {
			batch.getBucket(key).getAsync();
		}
		return batch.execute();
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> getBatchResponseListT(Class<T> clazz, String... keys) {
		RBatch batch = redisson.createBatch();
		for (String key : keys) {
			batch.getBucket(key).getAsync();
		}
		return (List<T>) batch.execute();
	}
	
	private <T> Map<String, RFuture<T>> getBatchRFutureMap(String... keys) {
		Map<String, RFuture<T>> futureMap = new HashMap<String, RFuture<T>>(keys.length);
		RBatch batch = redisson.createBatch();
		try {
			for (String key : keys) {
				RBucketAsync<T> bucket = batch.getBucket(key);
				futureMap.put(key, bucket.getAsync());
			}
		} catch (Exception e) {
			logger.error(e);
		}
		batch.execute();
		return futureMap;
	}
	
	private Map<String, Object> getBatchResponseMap(String... keys) {
		Map<String, Object> returnMap = new HashMap<String, Object>(keys.length);
		for (Map.Entry<String, RFuture<Object>> entry : getBatchRFutureMap(keys).entrySet()) {
			String key = entry.getKey();
			RFuture<Object> value = entry.getValue();
			Object object = null;
			try {
				object = value.get();
			} catch (Exception e) {
				logger.error(e);
			}
			returnMap.put(key, object);
		}
		return returnMap;
	}
	
	private <T> Map<String, T> getBatchResponseMapT(Class<T> clazz, String... keys) {
		Map<String, T> returnMap = new HashMap<String, T>(keys.length);
		Map<String, RFuture<T>> map = this.getBatchRFutureMap(keys);
		for (Map.Entry<String, RFuture<T>> entry : map.entrySet()) {
			String key = entry.getKey();
			RFuture<T> value = entry.getValue();
			T object = null;
			try {
				object = value.get();
			} catch (Exception e) {
				logger.error(e);
			}
			returnMap.put(key, object);
		}
		return returnMap;
	}
}