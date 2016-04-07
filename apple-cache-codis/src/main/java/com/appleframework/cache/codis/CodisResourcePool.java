package com.appleframework.cache.codis;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;

/**
 * @author cruise.xu
 * 
 */
public class CodisResourcePool {
	
	private JedisResourcePool jedisResourcePool;
		
	private String zkAddr;
	
	private int zkSessionTimeoutMs = 30000;
	
	private String zkProxyDir;
	
	public void init() {
		Version.logVersion();
		if(null == jedisResourcePool)
			jedisResourcePool = RoundRobinJedisPool.create()
		        .curatorClient(zkAddr, zkSessionTimeoutMs)
		        .zkProxyDir(zkProxyDir).build();
	}

	public String getZkAddr() {
		return zkAddr;
	}

	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public int getZkSessionTimeoutMs() {
		return zkSessionTimeoutMs;
	}

	public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
		this.zkSessionTimeoutMs = zkSessionTimeoutMs;
	}

	public String getZkProxyDir() {
		return zkProxyDir;
	}

	public void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}

	public JedisResourcePool getJedisResourcePool() {
		this.init();
		return jedisResourcePool;
	}
	
	public Jedis getResource() {
		this.init();
		return jedisResourcePool.getResource();
	}
	
}