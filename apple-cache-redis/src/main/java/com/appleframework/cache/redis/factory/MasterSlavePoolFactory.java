package com.appleframework.cache.redis.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.appleframework.cache.redis.balancer.LoadBalancer;
import com.appleframework.cache.redis.config.MasterSlaveServersConfig;
import com.appleframework.cache.redis.misc.URIBuilder;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MasterSlavePoolFactory extends PoolFactory {
			
	private MasterSlavePoolManager masterSlavePoolManager;

	private MasterSlaveServersConfig serverConfig;
	
	private JedisPoolConfig masterPoolConfig;
	
	private JedisPoolConfig slavePoolConfig;
	
	private LoadBalancer loadBalancer;
	
	public void setServerConfig(MasterSlaveServersConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public void setMasterPoolConfig(JedisPoolConfig masterPoolConfig) {
		this.masterPoolConfig = masterPoolConfig;
	}

	public void setSlavePoolConfig(JedisPoolConfig slavePoolConfig) {
		this.slavePoolConfig = slavePoolConfig;
	}

	public void init() {
		JedisPool masterPool = null;
		List<JedisPool> slavePools = new ArrayList<JedisPool>();
		if(null != serverConfig) {
			masterPool = new JedisPool(masterPoolConfig, 
					URIBuilder.create(serverConfig.getMasterAddress(), serverConfig.getDatabase(), serverConfig.getPassword()));
			Set<String> slaveConfigSet = serverConfig.getSlaveAddresses();
			for (String slaveUri : slaveConfigSet) {
				JedisPool slavePool = new JedisPool(slavePoolConfig, 
						URIBuilder.create(slaveUri, serverConfig.getDatabase(), serverConfig.getPassword()));
				slavePools.add(slavePool);
			}
			loadBalancer = serverConfig.getLoadBalancer();
		}
		else {
			masterPool = new JedisPool();
		}
		masterSlavePoolManager = new MasterSlavePoolManager(masterPool, slavePools);
	}

	public JedisPool getWritePool() {
		return masterSlavePoolManager.getMasterPool();
	}
	
	public JedisPool getReadPool() {
		List<JedisPool> list = masterSlavePoolManager.getSlavePoolList();
		if(list.size() > 0) {
			return loadBalancer.getJedisPool(list);
		}
		else {
			return masterSlavePoolManager.getMasterPool();
		}
	}
	
	public void destroy() {
		masterSlavePoolManager.destroy();
	}

}