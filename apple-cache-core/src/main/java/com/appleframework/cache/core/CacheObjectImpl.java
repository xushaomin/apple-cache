package com.appleframework.cache.core;

import java.io.Serializable;

public class CacheObjectImpl implements CacheObject, Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final long TIME_TO_LIVE_MS = 2592000000L;
	
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
	
	public void setExpiredSecond(int expiredSecond) {
		if(expiredSecond > 0) {
			this.expiredTime = (System.currentTimeMillis() + expiredSecond * 1000);
		} else {
			this.expiredTime = System.currentTimeMillis() + TIME_TO_LIVE_MS;
		}
	}

	public CacheObjectImpl(Object object, long expiredTime) {
		super();
		this.object = object;
		this.expiredTime = expiredTime;
	}
	
	public CacheObjectImpl(Object object, int expiredSecond) {
		super();
		this.object = object;
		if(expiredSecond > 0) {
			this.expiredTime = (System.currentTimeMillis() + expiredSecond * 1000);
		} else {
			this.expiredTime = System.currentTimeMillis() + TIME_TO_LIVE_MS;
		}
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
	
	public static CacheObjectImpl create(Object object, int expiredSecond) {
		return new CacheObjectImpl(object, expiredSecond);
	}
	
	public static CacheObjectImpl create(Object object, long expiredTime) {
		return new CacheObjectImpl(object, expiredTime);
	}
}
