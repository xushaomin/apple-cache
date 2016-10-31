package com.appleframework.cache.core;

public interface CacheObject {

	Object getObject();
	
	long getExpiredTime();
	
	boolean isExpired();
	
	void setExpiredTime(long expiredTime);
	
	void setExpiredSecond(int expiredSecond);

}
