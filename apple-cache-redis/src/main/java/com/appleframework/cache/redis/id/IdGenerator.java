package com.appleframework.cache.redis.id;

public interface IdGenerator {

	public long next(String tab);

	public long next(String tab, long shardId);
}