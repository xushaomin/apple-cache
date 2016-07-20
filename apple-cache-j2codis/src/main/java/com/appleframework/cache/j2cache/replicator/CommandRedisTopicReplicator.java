package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.core.utils.SerializeUtility;
import com.appleframework.cache.redis.factory.PoolFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class CommandRedisTopicReplicator implements CommandReplicator {
	
	private static Logger logger = Logger.getLogger(CommandRedisTopicReplicator.class);
	
	private String name = "J2_CACHE_MANAGER";

	private PoolFactory poolFactory;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPoolFactory(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}
	
	@SuppressWarnings("deprecation")
	public void replicate(Command command) {
		logger.warn("send command: " + command);
		JedisPool jedisPool = poolFactory.getWritePool();
		Jedis jedis = jedisPool.getResource();
		if (null != command) {
			try {
				logger.warn("The publish channel is " + name);
				Long o = jedis.publish(name.getBytes(), SerializeUtility.serialize(command));
				logger.info(o);
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				jedisPool.returnResource(jedis);
			}
		}
	}
}
