package com.appleframework.cache.caffeine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

@SuppressWarnings("unchecked")
public class CaffeineCacheManager implements CacheManager {

	private static Logger logger = LoggerFactory.getLogger(CaffeineCacheManager.class);

	private Long maximumSize = Long.MAX_VALUE;

	private LoadingCache<String, Serializable> loadingCache;

	public void setMaximumSize(Long maximumSize) {
		this.maximumSize = maximumSize;
	}
	
	public CacheLoader<String, Serializable> cacheLoader() {
        CacheLoader<String, Serializable> cacheLoader = new CacheLoader<String, Serializable>() {
            @Override
            public Serializable load(String key) throws Exception {
                return null;
            }
            @Override
            public Serializable reload(String key, Serializable oldValue) throws Exception {
                return null;
            }
        };
        return cacheLoader;
    }

	public void init() {
		CacheLoader<String, Serializable> cacheLoader = this.cacheLoader();
		loadingCache = Caffeine.newBuilder().expireAfter(CacheExpiryUtil.instance()).maximumSize(maximumSize).build(cacheLoader);
	}

	public LoadingCache<String, Serializable> getLoadingCache() {
		return loadingCache;
	}

	public void clear() throws CacheException {
		try {
			getLoadingCache().invalidateAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return getLoadingCache().get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}

	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			T value = null;
			Object element = getLoadingCache().get(key);
			if (null != element) {
				value = (T) element;
			}
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			getLoadingCache().invalidate(key);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public void set(String key, Object value) throws CacheException {
		if (null != value) {
			try {
				getLoadingCache().put(key, (Serializable)value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void expire(String key, int expireTime) throws CacheException {
		try {
			CacheExpiry.setExpiry(key, expireTime);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void set(String key, Object value, int expireTime) throws CacheException {
		if (null != value) {
			try {
				CacheExpiry.setExpiry(key, expireTime);
				getLoadingCache().put(key, (Serializable)value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		try {
			List<Object> list = new ArrayList<Object>();
			Map<String, Serializable> map = this.getLoadingCache().getAll(keyList);
			for (String key : keyList) {
				Object value = map.get(key);
				if (null != value) {
					list.add(value);
				} else {
					list.add(null);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		return this.getList(Arrays.asList(keys));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		try {
			List<T> list = new ArrayList<T>();
			Map<String, Serializable> map = this.getLoadingCache().getAll(keyList);
			for (String key : keyList) {
				Object value = map.get(key);
				if (null != value) {
					list.add((T) value);
				} else {
					list.add(null);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		return this.getList(clazz, Arrays.asList(keys));
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		try {
			Map<String, Serializable> cacheMap = this.getLoadingCache().getAll(keyList);
			Map<String, Object> returnMap = new HashMap<String, Object>();
			for (Map.Entry<String, Serializable> m : cacheMap.entrySet()) {
				String key = m.getKey();
				Serializable vaule = m.getValue();
				returnMap.put(key, vaule);
			}
			return returnMap;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		return this.getMap(Arrays.asList(keys));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		try {
			Map<String, T> map = new HashMap<String, T>();
			Map<String, Serializable> cacheMap = this.getLoadingCache().getAll(keyList);
			for (String key : keyList) {
				Object value = cacheMap.get(key);
				if (null != value) {
					map.put(key, (T) value);
				} else {
					map.put(key, null);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		return this.getMap(clazz, Arrays.asList(keys));
	}

}