package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.CommandReceiver;
import com.appleframework.cache.redis.factory.PoolFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class CommandRedisTopicReceiver implements CommandReceiver {
	
	protected final static Logger logger = Logger.getLogger(CommandRedisTopicReceiver.class);
	
	private String name = "J2_CACHE_MANAGER";
	
	private PoolFactory poolFactory;
		
	private CommandRedisTopicProcesser commandProcesser;
	
	private Thread threadSubscribe;
		
	public void init() {
		threadSubscribe = new Thread(new Runnable() {
			@Override
			public void run() {
				JedisPool jedisPool = poolFactory.getWritePool();
				Jedis jedis = jedisPool.getResource();
				try {
					logger.warn("The subscribe channel is " + name);
					jedis.subscribe(commandProcesser, name.getBytes());
				} catch (Exception e) {
					logger.error("Subscribing failed.", e);
				}
			}
		});
		threadSubscribe.start();
	}

	@Override
	public void onMessage(Command command) {
		commandProcesser.onProcess(command);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPoolFactory(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	public void setCommandProcesser(CommandRedisTopicProcesser commandProcesser) {
		this.commandProcesser = commandProcesser;
	}
}
