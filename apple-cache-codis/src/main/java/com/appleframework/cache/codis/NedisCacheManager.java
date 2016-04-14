/*package com.appleframework.cache.codis;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.CacheException;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.wandoulabs.nedis.NedisClient;
import com.wandoulabs.nedis.protocol.SetParams;

import io.netty.util.concurrent.Future;

public class NedisCacheManager implements CacheManager {

	private static Logger logger = Logger.getLogger(NedisCacheManager.class);

	private NedisResourcePool nedisResourcePool;

	public void setNedisResourcePool(NedisResourcePool nedisResourcePool) {
		this.nedisResourcePool = nedisResourcePool;
	}

	public void clear() throws CacheException {
		try {
			NedisClient nedis = nedisResourcePool.getNedisClient();
			String pattern = "*";
			Future<List<byte[]>> keysFuture = nedis.keys(SerializeUtility.serialize(pattern));
			List<byte[]> keys = keysFuture.get();
			for (byte[] key : keys) {
				nedis.del(key);
			}
			nedis.close().sync();
		} catch (InterruptedException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		} catch (ExecutionException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		}
	}

	public Object get(String key) throws CacheException {
		try {
			NedisClient nedis = nedisResourcePool.getNedisClient();
			byte[] value = nedis.get(SerializeUtility.serialize(key)).sync().getNow();
			nedis.close().sync();
			return SerializeUtility.unserialize(value);
		} catch (InterruptedException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T get(String key, Class<T> clazz) throws CacheException {
		try {
			NedisClient nedis = nedisResourcePool.getNedisClient();
			Future<byte[]> value = nedis.get(SerializeUtility.serialize(key));
			nedis.close().sync();
			return (T) SerializeUtility.unserialize(value.getNow());
		} catch (InterruptedException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		}
	}

	public boolean remove(String key) throws CacheException {
		try {
			NedisClient nedis = nedisResourcePool.getNedisClient();
			nedis.del(SerializeUtility.serialize(key)).sync();
			nedis.close().sync();
			return true;
		} catch (InterruptedException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		}
	}

	public void set(String key, Object obj) throws CacheException {
		try {
			NedisClient nedis = nedisResourcePool.getNedisClient();
			nedis.set(SerializeUtility.serialize(key), SerializeUtility.serialize(obj)).sync();
			nedis.close().sync();
		} catch (IllegalStateException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		}
	}

	public void set(String key, Object obj, int expireTime) throws CacheException {
		try {
			NedisClient nedis = nedisResourcePool.getNedisClient();
			SetParams params = new SetParams();
			params.setEx(expireTime);
			nedis.set(SerializeUtility.serialize(key), SerializeUtility.serialize(obj), params).sync();
			nedis.close().sync();
		} catch (InterruptedException e) {
			logger.error(e);
			throw new CacheException(e.getMessage(), e);
		}
	}

}*/