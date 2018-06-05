package com.appleframework.cache.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager2;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

@SuppressWarnings({ "unchecked", "deprecation" })
public class SingleJedisBucketCacheManager2 extends SingleJedisBucketCacheManager implements CacheManager2 {

	private static Logger logger = Logger.getLogger(SingleJedisBucketCacheManager2.class);

	private Map<String, Response<byte[]>> pipeline(Jedis jedis, String wkey) {
		Set<byte[]> set = jedis.keys(wkey.getBytes());
		Map<String, Response<byte[]>> responses = new HashMap<String, Response<byte[]>>(set.size());
		Pipeline pipeline = jedis.pipelined();
		for (byte[] bs : set) {
			String key = new String(bs);
			responses.put(key, pipeline.get(bs));
		}
		pipeline.sync();
		return responses;
	}
	
	@Override
	public List<Object> getLists(String wkey) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			List<Object> list = new ArrayList<Object>();
		    Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					list.add(SerializeUtility.unserialize(value));
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public <T> List<T> getLists(Class<T> clazz, String wkey) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<T>();
		    Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					list.add((T)SerializeUtility.unserialize(value));
				}
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public Map<String, Object> getMaps(String wkey) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
		    Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					map.put(key, SerializeUtility.unserialize(value));
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public <T> Map<String, T> getMaps(Class<T> clazz, String wkey) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			Map<String, T> map = new HashMap<String, T>();
		    Map<String, Response<byte[]>> responses = this.pipeline(jedis, wkey);
			
			for(String key : responses.keySet()) {
				Response<byte[]> response = responses.get(key);
				byte[] value = response.get();
				if(null != value) {
					map.put(key, (T)SerializeUtility.unserialize(value));
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new CacheException(e.getMessage());
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	
}