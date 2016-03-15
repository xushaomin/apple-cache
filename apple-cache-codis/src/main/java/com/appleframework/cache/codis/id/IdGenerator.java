package com.appleframework.cache.codis.id;

public interface IdGenerator {

	public long next(String tab);

	public long next(String tab, long shardId);
}