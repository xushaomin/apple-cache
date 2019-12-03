package com.appleframework.cache.redis.manager;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.utils.ReflectionUtility;

@Component
public class RedisHmsetCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(RedisHmsetCacheManager.class);
	
	private static Map<String, List<Object>> STR_MAP = new HashMap<>();
		
	private static Map<String, PropertyDescriptor[]> DESC_MAP = new HashMap<>();
	
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	private String name = "AC:";

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private String getKey(String key) {
		return name + key;
	}
	
	private void initProperties(Class<?> clazz) {
		String className = clazz.getName();
		if(null == STR_MAP.get(className)) {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(clazz);
			List<Object> resPropertieList = new ArrayList<Object>();
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if(!"class".equals(name)) {
					resPropertieList.add(name);
				}
			}
			STR_MAP.put(className, resPropertieList);
			DESC_MAP.put(className, descriptors);
		}
	}
		
	protected List<Object> getProperties(Class<?> clazz) {
		String className = clazz.getName();
		List<Object> strList = STR_MAP.get(className);
		if(null == strList) {
			initProperties(clazz);
			return STR_MAP.get(className);
		}
		else {
			return strList;
		}
	}
	
	private PropertyDescriptor[] getPropertyDescriptors(Object object) {
		String className = object.getClass().getName();
		PropertyDescriptor[] descriptors = DESC_MAP.get(className);
		if(null == descriptors) {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			descriptors = propertyUtilsBean.getPropertyDescriptors(object);
			DESC_MAP.put(className, descriptors);
		}
		return descriptors;
	}
	
	public void clear() throws CacheException {
		try {
			//jedis.del(getKey("*"));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		try {
			return redisTemplate.opsForHash().entries(getKey(key));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}
	
	private boolean isListNull(List<?> list) {
		if(null == list || list.size() == 0)
			return true;
		boolean isNull = true;
		for (Object object : list) {
			if(null != object)
				isNull = false;
		}
		return isNull;
	}

	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			List<Object> hashKeys = this.getProperties(clazz);
			List<Object> returnList = redisTemplate.opsForHash().multiGet(getKey(key), hashKeys);
			T object = clazz.newInstance();
			if (!isListNull(returnList)) {
				for (int i = 0; i < hashKeys.size(); i++) {
					Object boKey = hashKeys.get(i);
					Object boValue = returnList.get(i);
					if (null != boValue) {
						try {
							ReflectionUtility.invokeSetterMethod(object, boKey.toString(), boValue);
						} catch (Exception e) {
							logger.info(e.getMessage());
						}
					}
				}
				return object;
			} else {
				return null;
			}
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
	
	public void set(String key, Object obj) throws CacheException {
		if (null != obj) {
			try {
				Map<Object, Object> map = new HashMap<Object, Object>();
				PropertyDescriptor[] descriptors = this.getPropertyDescriptors(obj);
				for (int i = 0; i < descriptors.length; i++) {
					String name = descriptors[i].getName();
					if (!"class".equals(name)) {
						Object value = ReflectionUtility.invokeGetterMethod(obj, name);
						map.put(name, value);
					}
				}
				redisTemplate.opsForHash().putAll(getKey(key), map);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		if (null != obj) {
			try {
				Map<Object, Object> map = new HashMap<Object, Object>();
				PropertyDescriptor[] descriptors = this.getPropertyDescriptors(obj);
				for (int i = 0; i < descriptors.length; i++) {
					String name = descriptors[i].getName();
					if (!"class".equals(name)) {
						Object value = ReflectionUtility.invokeGetterMethod(obj, name);
						map.put(name, value);
					}
				}
				String mkey = getKey(key);
				redisTemplate.opsForHash().putAll(mkey, map);
				redisTemplate.expire(mkey, expireTime, TimeUnit.SECONDS);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public List<Object> getList(List<String> keyList) throws CacheException {
		return this.getList(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public List<Object> getList(String... keys) throws CacheException {
		return null;
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		return null;
	}

	@Override
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		return null;
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}