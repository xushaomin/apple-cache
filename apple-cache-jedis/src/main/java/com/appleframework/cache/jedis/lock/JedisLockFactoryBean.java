package com.appleframework.cache.jedis.lock;

import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.core.lock.Lock;
import com.appleframework.cache.jedis.factory.PoolFactory;

public class JedisLockFactoryBean implements FactoryBean<Lock> {

	private PoolFactory poolFactory;

	private long acquireTimeout = 60000, timeout = 10000;

	@Override
	public Lock getObject() throws Exception {
		Lock lock = new JedisLock(poolFactory, acquireTimeout, timeout);
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

	public void setAcquireTimeout(long acquireTimeout) {
		this.acquireTimeout = acquireTimeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
}
