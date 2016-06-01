package com.appleframework.cache.memcache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;


/**
 * @author cruise.xu
 * 
 */
public class XMemCachedManager implements CacheManager {

	private static Logger logger = Logger.getLogger(XMemCachedManager.class);

	private MemcachedClient memcachedClient;

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	public void clear() throws CacheException {
		try {
			memcachedClient.flushAll();
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (MemcachedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return memcachedClient.get(key);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (MemcachedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}
	
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			return memcachedClient.get(key);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (MemcachedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			return memcachedClient.delete(key);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (MemcachedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public void set(String key, Object obj) throws CacheException {
		if (null != obj) {
			try {
				Object o = memcachedClient.get(key);
				if (o == null)
					memcachedClient.add(key, 0, obj);
				else
					memcachedClient.set(key, 0, obj);
			} catch (TimeoutException e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			} catch (MemcachedException e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}

		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		if (null != obj) {
			try {
				if (memcachedClient.get(key) == null)
					memcachedClient.add(key, expireTime, obj);
				else
					memcachedClient.set(key, expireTime, obj);
			} catch (TimeoutException e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			} catch (MemcachedException e) {
				logger.error(e.getMessage());
				throw new CacheException(e.getMessage());
			}
		}
	}

	//批量获取
	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		List<Object> list = new ArrayList<Object>();
		Map<String, Object> map = this.getMap(keyList);		
		for (String key : keyList) {
			list.add(map.get(key));
		}
		return list;
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		List<Object> list = new ArrayList<Object>();
		Map<String, Object> map = this.getMap(keys);		
		for (String key : keys) {
			list.add(map.get(key));
		}
		return list;
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		List<T> list = new ArrayList<T>();
		Map<String, T> map = this.getMap(clazz, keys);		
		for (String key : keys) {
			list.add(map.get(key));
		}
		return list;
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		try {
			List<String> keyList = new ArrayList<>(keys.length);
			for (String key : keys) {
				keyList.add(key);
			}
			return memcachedClient.get(keyList);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (MemcachedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		try {
			List<String> keyList = new ArrayList<>(keys.length);
			for (String key : keys) {
				keyList.add(key);
			}
			return memcachedClient.get(keyList);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} catch (MemcachedException e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

}