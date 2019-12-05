package com.appleframework.cache.jedis.manager.sentinel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager2;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

@SuppressWarnings("unchecked")
public class JedisSentinelBucketCacheManager2 extends JedisSentinelBucketCacheManager implements CacheManager2 {

	private static Logger logger = LoggerFactory.getLogger(JedisSentinelBucketCacheManager2.class);

	private Map<String, Response<byte[]>> pipeline(Jedis jedis, String wkey) {
		Set<byte[]> set = jedis.keys(wkey.getBytes());
		Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(set.size());
		Pipeline pipeline = jedis.pipelined();
		for (byte[] bs : set) {
			String pkey = new String(bs);
			responses.put(pkey, pipeline.get(bs));
		}
		pipeline.sync();
		return responses;
	}
	
	@Override
	public List<Object> getLists(String wkey) throws CacheException {
		Jedis jedis = super.getJedis();
		try {
			List<Object> list = new ArrayList<Object>();
			Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			for(String pkey : responses.keySet()) {
				Response<byte[]> response = responses.get(pkey);
				byte[] value = response.get();
				if(null != value) {
					list.add(SerializeUtility.unserialize(value));
				}
				else {
					list.add(null);
				}
			}			
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public <T> List<T> getLists(Class<T> clazz, String wkey) throws CacheException {
		Jedis jedis = super.getJedis();
		try {
			List<T> list = new ArrayList<T>();
			Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			for(String pkey : responses.keySet()) {
				Response<byte[]> response = responses.get(pkey);
				byte[] value = response.get();
				if(null != value) {
					list.add((T)SerializeUtility.unserialize(value));
				}
				else {
					list.add(null);
				}
			}			
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, Object> getMaps(String wkey) throws CacheException {
		Jedis jedis = super.getJedis();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					map.put(key, SerializeUtility.unserialize(value));
				}
				else {
					map.put(key, null);
				}
			}			
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public <T> Map<String, T> getMaps(Class<T> clazz, String wkey) throws CacheException {
		Jedis jedis = super.getJedis();
		try {
			Map<String, T> map = new HashMap<String, T>();
			Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					map.put(key, (T)SerializeUtility.unserialize(value));
				}
				else {
					map.put(key, null);
				}
			}			
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	

}