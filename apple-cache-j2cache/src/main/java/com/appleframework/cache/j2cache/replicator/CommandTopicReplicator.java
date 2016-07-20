package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RTopic;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.CommandReplicator;
import com.appleframework.cache.j2cache.utils.Contants;

public class CommandTopicReplicator implements CommandReplicator {
	
	private static Logger logger = Logger.getLogger(CommandTopicReplicator.class);
	
	private String name = "J2_CACHE_MANAGER";

	private RedissonClient redisson;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
	
	private RTopic<Command> topic;
	
	public void init() {
		topic = redisson.getTopic(Contants.TOPIC_PREFIX_KEY + name);
	}

	public void replicate(Command command) {
		logger.warn("send command: " + command);
		if (null != command) {
			try {
				logger.warn("The publish channel is " + name);
				Long o = topic.publish(command);
				logger.info(o);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}
