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
import com.appleframework.cache.core.utils.ReflectionUtility;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.jedis.factory.JedisClusterFactory;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Response;

public class JedisClusterHmsetCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(JedisClusterHmsetCacheManager.class);
	
	private static Map<String, String[]> STR_MAP = new HashMap<>();
	
	private static Map<String, byte[][]> BYTE_MAP = new HashMap<>();
	
	private static Map<String, PropertyDescriptor[]> DESC_MAP = new HashMap<>();
	
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
	
	private byte[] getKey(String key) {
		return (name + key).getBytes();
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
			BYTE_MAP.put(className, byteProperties);
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
	
	private byte[][] getByteProperties(Class<?> clazz) {
		String className = clazz.getName();
		byte[][] bytes = BYTE_MAP.get(className);
		if(null == bytes) {
			initProperties(clazz);
			return BYTE_MAP.get(className);
		}
		else {
			return bytes;
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
			Map<byte[], byte[]> value = jedis.hgetAll(getKey(key));
			Map<String, Object> map = new HashMap<>();
			if (null != value) {
				for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
					String bokey = new String(entry.getKey());
					Object boValue = (Object) SerializeUtility.unserialize(entry.getValue());
					map.put(bokey, boValue);
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
			byte[][] byteProperties = this.getByteProperties(clazz);
			List<byte[]> list = jedis.hmget(getKey(key), byteProperties);
			T object = clazz.newInstance();
			if (!isListNull(list)) {
				String[] stringFields = this.getStrProperties(clazz);
				for (int i = 0; i < stringFields.length; i++) {
					String boKey = stringFields[i];
					Object boValue = (Object) SerializeUtility.unserialize(list.get(i));
					if (null != boValue) {
						try {
							ReflectionUtility.invokeSetterMethod(object, boKey, boValue);
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
				Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
				PropertyDescriptor[] descriptors = this.getPropertyDescriptors(obj);
				for (int i = 0; i < descriptors.length; i++) {
					String name = descriptors[i].getName();
					if (!"class".equals(name)) {
						Object value = ReflectionUtility.invokeGetterMethod(obj, name);
						map.put(name.getBytes(), SerializeUtility.serialize(value));
					}
				}
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
				byte[] byteKey = getKey(key);
				Map<byte[], byte[]> map = new HashMap<>();
				PropertyDescriptor[] descriptors = this.getPropertyDescriptors(obj);
				for (int i = 0; i < descriptors.length; i++) {
					String name = descriptors[i].getName();
					if (!"class".equals(name)) {
						Object value = ReflectionUtility.invokeGetterMethod(obj, name);
						map.put(name.getBytes(), SerializeUtility.serialize(value));
					}
				}
				jedis.hmset(byteKey, map);
				jedis.expire(byteKey, expireTime);
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
			Map<String, Response<Map<byte[], byte[]>>> responses 
				= new HashMap<String, Response<Map<byte[], byte[]>>>(keys.length);

			pipeline.refreshCluster();
			for (String key : keys) {
				responses.put(key, pipeline.hgetAll(getKey(key)));
			}
			pipeline.sync();

			for (String key : responses.keySet()) {
				Response<Map<byte[], byte[]>> response = responses.get(key);
				Map<byte[], byte[]> value = response.get();
				if (null != value) {
					Map<String, Object> object = new HashMap<>();
					for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
						String boKey = new String(entry.getKey());
						Object boValue = (Object) SerializeUtility.unserialize(entry.getValue());
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
			Map<String, Response<List<byte[]>>> responses 
				= new HashMap<String, Response<List<byte[]>>>(keys.length);
			
			pipeline.refreshCluster();
			byte[][] byteProperties = this.getByteProperties(clazz);
			for (String key : keys) {
				Response<List<byte[]>> response = pipeline.hmget(getKey(key), byteProperties);
				responses.put(key, response);
			}
			pipeline.sync();

			String[] stringFields = this.getStrProperties(clazz);
			for (String key : responses.keySet()) {
				Response<List<byte[]>> response = responses.get(key);
				List<byte[]> value = response.get();
				if (!isListNull(value)) {
					T object = clazz.newInstance();
					for (int i = 0; i < stringFields.length; i++) {
						String boKey = stringFields[i];
						Object boValue = (Object) SerializeUtility.unserialize(value.get(i));
						if (null != boValue) {
							try {
								ReflectionUtility.invokeSetterMethod(object, boKey, boValue);
							} catch (Exception e) {
								logger.info(e.getMessage());
							}
						}
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
	public Map<String, Object> getMap(List<String> keyList) throws CacheException {
		return this.getMap(keyList.toArray(new String[keyList.size()]));
	}

	@Override
	public Map<String, Object> getMap(String... keys) throws CacheException {
		try {
			Map<String, Object> map = new HashMap<>();
			Map<String, Response<Map<byte[], byte[]>>> responses = new HashMap<String, Response<Map<byte[], byte[]>>>(
					keys.length);
			
			pipeline.refreshCluster();
			for (String key : keys) {
				responses.put(key, pipeline.hgetAll(getKey(key)));
			}
			pipeline.sync();

			for (String key : responses.keySet()) {
				Response<Map<byte[], byte[]>> response = responses.get(key);
				Map<byte[], byte[]> value = response.get();
				if (null != value) {
					Map<String, Object> mmap = new HashMap<>();
					for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
						String field = new String(entry.getKey());
						Object val = (Object) SerializeUtility.unserialize(entry.getValue());
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
			Map<String, Response<List<byte[]>>> responses 
				= new HashMap<String, Response<List<byte[]>>>(keys.length);
			
			pipeline.refreshCluster();
			byte[][] byteProperties = this.getByteProperties(clazz);
			for (String key : keys) {
				responses.put(key, pipeline.hmget(getKey(key), byteProperties));
			}
			pipeline.sync();

			String[] stringFields = this.getStrProperties(clazz);
			for (String key : responses.keySet()) {
				Response<List<byte[]>> response = responses.get(key);
				List<byte[]> value = response.get();
				if (null != value) {
					T object = clazz.newInstance();
					for (int i = 0; i < stringFields.length; i++) {
						String boKey = stringFields[i];
						Object boValue = (Object) SerializeUtility.unserialize(value.get(i));
						if (null != boValue) {
							try {
								ReflectionUtility.invokeSetterMethod(object, boKey, boValue);
							} catch (Exception e) {
								logger.info(e.getMessage());
							}
						}
					}
					map.put(key, object);
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