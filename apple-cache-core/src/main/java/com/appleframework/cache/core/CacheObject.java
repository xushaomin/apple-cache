package com.appleframework.cache.core;

import java.io.Serializable;

public interface CacheObject extends Serializable {

	Object getObject();
	
	long getExpiredTime();
	
	boolean isExpired();
	
	void setExpiredTime(long expiredTime);
	
	void setExpiredSecond(int expiredSecond);

}
