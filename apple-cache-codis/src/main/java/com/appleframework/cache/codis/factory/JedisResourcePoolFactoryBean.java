package com.appleframework.cache.codis.factory;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.codis.Version;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisResourcePoolFactoryBean implements FactoryBean<JedisResourcePool> {

	private String zkAddr;
	private String zkProxyDir;

    private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
    private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
    private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;

	private int database = Protocol.DEFAULT_DATABASE;
	private String password;
	
    private int timeoutMs = Protocol.DEFAULT_TIMEOUT;
    
	private int zkSessionTimeoutMs = 30000;
	
	private boolean testOnBorrow = true;
	private boolean testOnCreate = true;
	private boolean testOnReturn = true;
	private boolean testWhileIdle = true;

	@Override
	public JedisResourcePool getObject() throws Exception {
		Version.logVersion();
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMinIdle(minIdle);
		config.setMaxIdle(maxIdle);
		config.setMaxTotal(maxTotal);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnCreate(testOnCreate);
		config.setTestOnReturn(testOnReturn);
		config.setTestWhileIdle(testWhileIdle);

		return RoundRobinJedisPool.create()
				.curatorClient(zkAddr, zkSessionTimeoutMs)
				.zkProxyDir(zkProxyDir)
				.poolConfig(config)
				.database(database)
				.password(password)
				.timeoutMs(timeoutMs)
				.build();
	}

	@Override
	public Class<JedisResourcePool> getObjectType() {
		return JedisResourcePool.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
		this.zkSessionTimeoutMs = zkSessionTimeoutMs;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public void setTestOnCreate(boolean testOnCreate) {
		this.testOnCreate = testOnCreate;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

}
