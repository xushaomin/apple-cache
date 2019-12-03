package com.appleframework.cache.codis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleframework.cache.codis.CodisResourcePool;

import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Protocol;

@Configuration
public class CodisResourcePoolConfig {

	@Value("${spring.codis.zkAddr}")
	private String zkAddr;
	
	@Value("${spring.codis.zkProxyDir}")
	private String zkProxyDir;

    @Value("${spring.codis.maxTotal:8")
    private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
    
    @Value("${spring.codis.maxIdle:8}")
    private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
    
    @Value("${spring.codis.minIdle:0}")
    private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;

    @Value("${spring.codis.database:0}")
	private int database = Protocol.DEFAULT_DATABASE;
	
    @Value("${spring.codis.password:}")
	private String password;
	
    @Value("${spring.codis.timeoutMs:2000}")
    private int timeoutMs = Protocol.DEFAULT_TIMEOUT;
    
    @Value("${spring.codis.zkSessionTimeoutMs:30000}")
	private int zkSessionTimeoutMs = 30000;
	
	@Value("${spring.codis.testOnBorrow:true}")
	private boolean testOnBorrow = true;
	
	@Value("${spring.codis.testOnCreate:true}")
	private boolean testOnCreate = true;
	
	@Value("${spring.codis.testOnReturn:true}")
	private boolean testOnReturn = true;
	
	@Value("${spring.codis.testWhileIdle:true}")
	private boolean testWhileIdle = true;

	@Bean
	@ConditionalOnMissingBean(RoundRobinJedisPool.class)
	public CodisResourcePool codisResourcePoolFactory() {
		CodisResourcePool pool = new CodisResourcePool();
		pool.setDatabase(database);
		pool.setMaxIdle(maxIdle);
		pool.setMaxTotal(maxTotal);
		pool.setMinIdle(minIdle);
		pool.setPassword(password);
		pool.setTestOnBorrow(testOnBorrow);
		pool.setTestOnCreate(testOnCreate);
		pool.setTestOnReturn(testOnReturn);
		pool.setTestWhileIdle(testWhileIdle);
		pool.setTimeoutMs(timeoutMs);
		pool.setZkAddr(zkAddr);
		pool.setZkProxyDir(zkProxyDir);
		pool.setZkSessionTimeoutMs(zkSessionTimeoutMs);
		pool.init();
		return pool;
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
