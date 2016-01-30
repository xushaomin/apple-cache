package com.appleframework.cache.redis.id;

import org.redisson.RedissonClient;
import org.redisson.core.RAtomicLong;

public class RedissonIdGenerator implements IdGenerator {

	private RedissonClient redisson;

	private int retryTimes = 3;

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
		RAtomicLong atomicLong = redisson.getAtomicLong(tab);
		long id = atomicLong.getAndIncrement();
		if(id >= shardId) {
			return id;
		}
		else {
			atomicLong.set(shardId);
			return shardId;
		}
	}

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
	
}