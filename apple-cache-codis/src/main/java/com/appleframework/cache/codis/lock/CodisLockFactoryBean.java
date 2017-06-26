package com.appleframework.cache.codis.lock;

import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.lock.Lock;

public class CodisLockFactoryBean implements FactoryBean<Lock> {

	private CodisResourcePool codisResourcePool;

	private int timeoutMsecs = 10000, expireMsecs = 20000;

	@Override
	public Lock getObject() throws Exception {
		Lock lock = new CodisLock(codisResourcePool, timeoutMsecs, expireMsecs);
		return lock;
	}

	@Override
	public Class<Lock> getObjectType() {
		return Lock.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setCodisResourcePool(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	public void setTimeoutMsecs(int timeoutMsecs) {
		this.timeoutMsecs = timeoutMsecs;
	}

	public void setExpireMsecs(int expireMsecs) {
		this.expireMsecs = expireMsecs;
	}

}
