package com.appleframework.cache.core;

import java.io.Serializable;

public class CacheObjectImpl implements CacheObject, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Object object;
	
	private long expiredTime;

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public CacheObjectImpl(Object object, long expiredTime) {
		super();
		this.object = object;
		this.expiredTime = expiredTime;
	}

	public CacheObjectImpl() {
		super();
	}

	@Override
	public boolean isExpired() {
		if(System.currentTimeMillis() - expiredTime > 0)
			return true;
		else
			return false;
	}
	
}
