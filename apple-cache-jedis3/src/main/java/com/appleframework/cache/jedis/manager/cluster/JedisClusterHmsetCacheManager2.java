package com.appleframework.cache.jedis.manager.cluster;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.jedis.factory.JedisClusterFactory;
import com.appleframework.cache.jedis.utils.BeanMapUtil;
import com.appleframework.cache.jedis.utils.ObjectMapUtil;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Response;

public class JedisClusterHmsetCacheManager2 implements CacheManager {

	private static Logger logger = Logger.getLogger(JedisClusterHmsetCacheManager2.class);
	
	private static Map<String, String[]> STR_MAP = new HashMap<>();
		
	private JedisClusterFactory connectionFactory;
	
	private JedisClusterPipeline pipeline;
	
	private String name = "AC:";	
	
	public void init() {
		JedisCluster jedis = this.getJedis();
		pipeline = JedisClusterPipeline.pipelined(jedis);
	}

	public void setConnectionFactory(JedisClusterFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	private JedisCluster getJedis() {
		return connectionFactory.getClusterConnection();
	}
	
	private String getKey(String key) {
		return name + key;
	}
	
	private void initProperties(Class<?> clazz) {
		String className = clazz.getName();
		if(null == STR_MAP.get(className)) {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(clazz);
			List<String> resPropertieList = new ArrayList<String>();
			String[] strProperties = new String[descriptors.length - 1];
			byte[][] byteProperties = new byte[descriptors.length - 1][];
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if(!"class".equals(name)) {
					resPropertieList.add(name);
				}
			}
			for (int i = 0; i < resPropertieList.size(); i++) {
				String name = resPropertieList.get(i);
				strProperties[i] = name;
				byteProperties[i] = name.getBytes();
			}
			STR_MAP.put(className, strProperties);
		}
	}
	
	private String[] getStrProperties(Class<?> clazz) {
		String className = clazz.getName();
		String[] strs = STR_MAP.get(className);
		if(null == strs) {
			initProperties(clazz);
			return STR_MAP.get(className);
		}
		else {
			return strs;
		}
	}
	
	public void clear() throws CacheException {
		JedisCluster jedis = this.getJedis();
		try {
			jedis.del(getKey("*"));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Object get(String key) throws CacheException {
		JedisCluster jedis = this.getJedis();
		try {
			Map<String, String> value = jedis.hgetAll(getKey(key));
			Map<String, Object> map = new HashMap<>();
			if (null != value) {
				for (Map.Entry<String, String> entry : value.entrySet()) {
					String bokey = entry.getKey();
					String boValue = entry.getValue();
					if(null != boValue) {
						map.put(bokey, boValue);
					}
				}
				return map;
			} else {
				return null;
			}
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
		JedisCluster jedis = this.getJedis();
		try {
			String[] strProperties = this.getStrProperties(clazz);
			List<String> list = jedis.hmget(getKey(key), strProperties);
			if (!isListNull(list)) {
				Map<String, String> data = new HashMap<>();
				String[] stringFields = this.getStrProperties(clazz);
				for (int i = 0; i < stringFields.length; i++) {
					String boKey = stringFields[i];
					String boValue = list.get(i);
					data.put(boKey, boValue);
				}
				return ObjectMapUtil.getObjectFromMap(clazz, data);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		}
	}

	public boolean remove(String key) throws CacheException {
		JedisCluster jedis = this.getJedis();
		try {
			return jedis.del(getKey(key))>0;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	@Override
	public void expire(String key, int timeout) throws CacheException {
		JedisCluster jedis = this.getJedis();
		try {
			jedis.expire(getKey(key), timeout);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void set(String key, Object obj) throws CacheException {
		JedisCluster jedis = this.getJedis();
		if (null != obj) {
			try {
				Map<String, String> map = BeanMapUtil.convertBean2(obj);
				jedis.hmset(getKey(key), map);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		JedisCluster jedis = this.getJedis();
		if (null != obj) {
			try {
				String strkey = getKey(key);
				Map<String, String> map = BeanMapUtil.convertBean2(obj);
				jedis.hmset(strkey, map);
				jedis.expire(strkey, expireTime);
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
		try {
			List<Object> list = new ArrayList<Object>();
			Map<String, Response<Map<String, String>>> responses 
				= new HashMap<String, Response<Map<String, String>>>(keys.length);

			pipeline.refreshCluster();
			for (String key : keys) {
				responses.put(key, pipeline.hgetAll(getKey(key)));
			}
			pipeline.sync();

			for (String key : responses.keySet()) {
				Response<Map<String, String>> response = responses.get(key);
				Map<String, String> value = response.get();
				if (null != value) {
					Map<String, String> object = new HashMap<>();
					for (Map.Entry<String, String> entry : value.entrySet()) {
						String boKey = entry.getKey();
						String boValue = entry.getValue();
						object.put(boKey, boValue);
					}
					list.add(object);
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
	public <T> List<T> getList(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getList(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String... keys) throws CacheException {
		try {
			List<T> list = new ArrayList<T>();
			Map<String, Response<List<String>>> responses 
				= new HashMap<String, Response<List<String>>>(keys.length);
			
			pipeline.refreshCluster();
			String[] byteProperties = this.getStrProperties(clazz);
			for (String key : keys) {
				Response<List<String>> response = pipeline.hmget(getKey(key), byteProperties);
				responses.put(key, response);
			}
			pipeline.sync();

			String[] stringFields = this.getStrProperties(clazz);
			for (String key : responses.keySet()) {
				Response<List<String>> response = responses.get(key);
				List<String> value = response.get();
				if (!isListNull(value)) {
					Map<String, String> data = new HashMap<>();
					for (int i = 0; i < stringFields.length; i++) {
						String boKey = stringFields[i];
						String boValue = value.get(i);
						if (null != boValue) {
							data.put(boKey, boValue);
						}
					}
					list.add(ObjectMapUtil.getObjectFromMap(clazz, data));
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
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		try {
			Map<String, Object> map = new HashMap<>();
			Map<String, Response<Map<String, String>>> responses = new HashMap<String, Response<Map<String, String>>>(
					keys.length);
			
			pipeline.refreshCluster();
			for (String key : keys) {
				responses.put(key, pipeline.hgetAll(getKey(key)));
			}
			pipeline.sync();

			for (String key : responses.keySet()) {
				Response<Map<String, String>> response = responses.get(key);
				Map<String, String> value = response.get();
				if (null != value) {
					Map<String, Object> mmap = new HashMap<>();
					for (Map.Entry<String, String> entry : value.entrySet()) {
						String field = entry.getKey();
						Object val = entry.getValue();
						mmap.put(field, val);
			        }  
					map.put(key, mmap);
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
	public <T> Map<String, T> getMap(Class<T> clazz, List<String> keyList) throws CacheException {
		return this.getMap(clazz, keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) throws CacheException {
		try {
			Map<String, T> map = new HashMap<>();
			Map<String, Response<List<String>>> responses 
				= new HashMap<String, Response<List<String>>>(keys.length);
			
			pipeline.refreshCluster();
			String[] strProperties = this.getStrProperties(clazz);
			for (String key : keys) {
				responses.put(key, pipeline.hmget(getKey(key), strProperties));
			}
			pipeline.sync();

			String[] stringFields = this.getStrProperties(clazz);
			for (String key : responses.keySet()) {
				Response<List<String>> response = responses.get(key);
				List<String> value = response.get();
				if (null != value) {
					Map<String, String> data = new HashMap<>();
					for (int i = 0; i < stringFields.length; i++) {
						String boKey = stringFields[i];
						String boValue = value.get(i);
						if (null != boValue) {
							data.put(boKey, boValue);
						}
					}
					map.put(key, ObjectMapUtil.getObjectFromMap(clazz, data));
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}