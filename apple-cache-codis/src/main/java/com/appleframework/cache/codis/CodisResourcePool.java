package com.appleframework.cache.codis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author cruise.xu
 * 
 */
public class CodisResourcePool {

	private JedisResourcePool jedisResourcePool;

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

	public void init() {
		Version.logVersion();
		if (null == jedisResourcePool) {

			JedisPoolConfig config = new JedisPoolConfig();
			config.setMinIdle(minIdle);
			config.setMaxIdle(maxIdle);
			config.setMaxTotal(maxTotal);
			config.setTestOnBorrow(testOnBorrow);
			config.setTestOnCreate(testOnCreate);
			config.setTestOnReturn(testOnReturn);
			config.setTestWhileIdle(testWhileIdle);
			
			jedisResourcePool = RoundRobinJedisPool
					.create()
					.curatorClient(zkAddr, zkSessionTimeoutMs)
					.zkProxyDir(zkProxyDir)
					.poolConfig(config)
					.database(database)
					.password(password)
					.timeoutMs(timeoutMs)
					.build();
		}
	}
	
	public JedisResourcePool getJedisResourcePool() {
		return jedisResourcePool;
	}

	public Jedis getResource() {
		return jedisResourcePool.getResource();
	}

	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
		this.zkSessionTimeoutMs = zkSessionTimeoutMs;
	}

	public void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setDatabase(int database) {
		this.database = database;
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

	public void setPassword(String password) {
		this.password = password;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

}