package com.appleframework.cache.caffeine.spring;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.CacheObject;
import com.appleframework.cache.core.CacheObjectImpl;
import com.appleframework.cache.core.config.SpringCacheConfig;
import com.appleframework.cache.core.spring.CacheOperation;

public class SpringCacheOperation implements CacheOperation {

	private static Logger logger = Logger.getLogger(SpringCacheOperation.class);

	private String name;
	private int expire = 0;

	private CacheManager cacheManager;

	private void init(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public SpringCacheOperation(CacheManager cacheManager, String name) {
		this.name = name;
		this.init(cacheManager);
	}

	public SpringCacheOperation(CacheManager cacheManager, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.init(cacheManager);
	}

	private String getKey(String key) {
		return name + "_" + key;
	}

	public Object get(String key) {
		String nkey = getKey(key);
		Object value = null;
		try {
			Object element = cacheManager.get(nkey);
			if (null != element) {
				if (SpringCacheConfig.isCacheObject()) {
					CacheObject cache = (CacheObject) element;
					if (null != cache) {
						if (cache.isExpired()) {
							this.resetCacheObject(key, cache);
						} else {
							value = cache.getObject();
						}
					}
				} else {
					value = element;
				}
			}
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
		return value;
	}

	private void resetCacheObject(String key, CacheObject cache) {
		try {
			String nkey = getKey(key);
			cacheManager.remove(nkey);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void put(String key, Object value) {
		String nkey = getKey(key);
		if (value == null)
			this.delete(key);
		try {
			if (SpringCacheConfig.isCacheObject()) {
				CacheObject object = CacheObjectImpl.create(value, expire);
				cacheManager.set(nkey, object);
			} else {
				if (expire > 0) {
					cacheManager.set(nkey, (Serializable) value, expire);
				} else {
					cacheManager.set(nkey, (Serializable) value);
				}
			}

		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void clear() {
		try {
			cacheManager.clear();
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void delete(String key) {
		try {
			String nkey = getKey(key);
			cacheManager.remove(nkey);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public int getExpire() {
		return expire;
	}

}
