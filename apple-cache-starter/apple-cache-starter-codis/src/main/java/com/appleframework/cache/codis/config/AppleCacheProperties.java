package com.appleframework.cache.codis.config;

import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import redis.clients.jedis.Protocol;

@ConfigurationProperties(prefix = "apple.cache.codis")
public class AppleCacheProperties {

	public static final String PREFIX = "apple.cache.codis";

	// cache
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

	// spring
	private boolean cacheEnable = true;

	private boolean cacheObject = true;

	private String cacheKeyPrefix;

	private Map<String, Integer> expireConfig;

	public String getZkAddr() {
		return zkAddr;
	}

	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public String getZkProxyDir() {
		return zkProxyDir;
	}

	public void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeoutMs() {
		return timeoutMs;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public int getZkSessionTimeoutMs() {
		return zkSessionTimeoutMs;
	}

	public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
		this.zkSessionTimeoutMs = zkSessionTimeoutMs;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnCreate() {
		return testOnCreate;
	}

	public void setTestOnCreate(boolean testOnCreate) {
		this.testOnCreate = testOnCreate;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isCacheEnable() {
		return cacheEnable;
	}

	public void setCacheEnable(boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
	}

	public boolean isCacheObject() {
		return cacheObject;
	}

	public void setCacheObject(boolean cacheObject) {
		this.cacheObject = cacheObject;
	}

	public String getCacheKeyPrefix() {
		return cacheKeyPrefix;
	}

	public void setCacheKeyPrefix(String cacheKeyPrefix) {
		this.cacheKeyPrefix = cacheKeyPrefix;
	}

	public Map<String, Integer> getExpireConfig() {
		return expireConfig;
	}

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		this.expireConfig = expireConfig;
	}

}
