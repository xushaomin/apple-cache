package com.appleframework.cache.jedis.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;

public class MasterSlavePoolManager {

	private static Logger logger = LoggerFactory.getLogger(MasterSlavePoolManager.class);

	private ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

	private List<JedisPool> okSlavePools = new ArrayList<JedisPool>();

	private List<JedisPool> slavePools = new ArrayList<JedisPool>();

	private JedisPool masterPool;

	public MasterSlavePoolManager(JedisPool masterPool, final List<JedisPool> slavePools) {
		this.masterPool = masterPool;
		this.slavePools.addAll(slavePools);
		this.okSlavePools = slavePools;
		this.init();
	}

	private void init() {
		// 启动一个线程每1秒钟slave连接池是否有问题
		exec.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				try {
					for (JedisPool jedisPool : slavePools) {
						boolean poolInvalid = false;
						try {
							jedisPool.getResource();
					    } catch (Exception e) {
					    	poolInvalid = true;
					    }
						if (poolInvalid) {
							if(okSlavePools.contains(jedisPool)) {
								okSlavePools.remove(jedisPool);
								logger.warn("remove jedisPool from okSlavePools successful!");
								logger.warn("okSlavePools.size=" + okSlavePools.size() + " and slavePools.size=" + slavePools.size());
							}
						}
						else {
							if(!okSlavePools.contains(jedisPool)) {
								okSlavePools.add(jedisPool);
								logger.warn("add jedisPool to okSlavePools successful!");
								logger.warn("okSlavePools.size=" + okSlavePools.size() + " and slavePools.size=" + slavePools.size());
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public List<JedisPool> getSlavePoolList() {
		return okSlavePools;
	}

	public JedisPool getMasterPool() {
		return masterPool;
	}
	
	public void destroy() {
		masterPool.destroy();
		if(slavePools.size() > 0) {
			for (JedisPool slavePool : slavePools) {
				slavePool.destroy();
			}
		}
	}

}