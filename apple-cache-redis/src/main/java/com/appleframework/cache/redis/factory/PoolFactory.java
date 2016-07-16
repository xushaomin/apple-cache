package com.appleframework.cache.redis.factory;

import redis.clients.jedis.JedisPool;

public abstract class PoolFactory {

	public abstract JedisPool getWritePool();
	
	public abstract JedisPool getReadPool();
}
