package com.appleframework.cache.jedis.lock;

import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.core.lock.Lock;
import com.appleframework.cache.jedis.factory.PoolFactory;

public class JedisLockFactoryBean implements FactoryBean<Lock> {

	private PoolFactory poolFactory;

	private int timeoutMsecs = 10000, expireMsecs = 20000;

	@Override
	public Lock getObject() throws Exception {
		Lock lock = new JedisLock(poolFactory, timeoutMsecs, expireMsecs);
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

	public void setPoolFactory(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	public void setTimeoutMsecs(int timeoutMsecs) {
		this.timeoutMsecs = timeoutMsecs;
	}

	public void setExpireMsecs(int expireMsecs) {
		this.expireMsecs = expireMsecs;
	}

}
