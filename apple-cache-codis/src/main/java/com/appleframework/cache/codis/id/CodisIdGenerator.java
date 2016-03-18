package com.appleframework.cache.codis.id;

import com.appleframework.cache.codis.CodisResourcePool;

import redis.clients.jedis.Jedis;

public class CodisIdGenerator implements IdGenerator {
	
	private int retryTimes = 3;
	
	private CodisResourcePool codisResourcePool;

	public synchronized long next(String tab) {
		return next(tab, 0);
	}

	public long next(String tab, long shardId) {
		for (int i = 0; i < retryTimes; ++i) {
			Long id = innerNext(tab, shardId);
			if (id != null) {
				return id;
			}
		}
		throw new RuntimeException("Can not generate id!");
	}

	private Long innerNext(String tab, long shardId) {
		Jedis jedis = getCodisResourcePool().getResource();
		long id = jedis.incr(tab);
		if (id < shardId) {
			id = jedis.incrBy(tab, shardId);
		}
		jedis.close();
		return id;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public CodisResourcePool getCodisResourcePool() {
		return codisResourcePool;
	}
	
}