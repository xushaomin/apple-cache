/*package com.appleframework.cache.codis;

import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;

import com.wandoulabs.nedis.NedisClient;
import com.wandoulabs.nedis.NedisClientPool;
import com.wandoulabs.nedis.NedisClientPoolBuilder;
import com.wandoulabs.nedis.codis.RoundRobinNedisClientPool;
import com.wandoulabs.nedis.util.NedisUtils;

import redis.clients.jedis.Protocol;

*//**
 * @author cruise.xu
 *//*
public class NedisResourcePool {
	
	private static Logger logger = Logger.getLogger(CodisCacheManager.class);

	private NedisClientPool nedisClientPool;

	private String zkAddr;
	private String zkProxyDir;

	private int maxPooledConns = Math.max(2, 2 * ManagementFactory.getOperatingSystemMXBean()
            .getAvailableProcessors());
	
	private int database = Protocol.DEFAULT_DATABASE;
	private String password;
	
    private int timeoutMs = Protocol.DEFAULT_TIMEOUT;
    
	private int zkSessionTimeoutMs = 30000;
	
	public void init() {
		Version.logVersion();
		if (null == nedisClientPool) {
			
			NedisClientPoolBuilder poolBuilder = NedisClientPoolBuilder
					.create()
					.timeoutMs(timeoutMs)
					.database(database)
					.maxPooledConns(maxPooledConns);
			if(null != password)
				poolBuilder.password(password);
			
			try {
				nedisClientPool = RoundRobinNedisClientPool.builder()
				        .poolBuilder(poolBuilder)
				        .curatorClient(zkAddr, zkSessionTimeoutMs)
				        .zkProxyDir(zkProxyDir).build().sync().getNow();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
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

	public void setDatabase(int database) {
		this.database = database;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public void setMaxPooledConns(int maxPooledConns) {
		this.maxPooledConns = maxPooledConns;
	}
	
	public NedisClientPool getNedisClientPool() {
		return nedisClientPool;
	}
	
	public NedisClient getNedisClient() {
		return NedisUtils.newPooledClient(nedisClientPool);
	}

}*/