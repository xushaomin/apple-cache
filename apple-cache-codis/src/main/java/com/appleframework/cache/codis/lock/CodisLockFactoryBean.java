package com.appleframework.cache.codis.lock;

import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.lock.Lock;

public class CodisLockFactoryBean implements FactoryBean<Lock> {

	private CodisResourcePool codisResourcePool;

	private long acquireTimeout = 60000, timeout = 10000;

	@Override
	public Lock getObject() throws Exception {
		Lock lock = new CodisLock(codisResourcePool, acquireTimeout, timeout);
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

	public void setAcquireTimeout(long acquireTimeout) {
		this.acquireTimeout = acquireTimeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
