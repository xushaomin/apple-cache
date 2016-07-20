package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.MessageListener;
import org.redisson.core.RTopic;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.CommandProcesser;
import com.appleframework.cache.core.replicator.CommandReceiver;
import com.appleframework.cache.j2cache.utils.Contants;

public class CommandTopicReceiver implements CommandReceiver {
	
	protected final static Logger logger = Logger.getLogger(CommandTopicReceiver.class);
	
	private String name = "J2_CACHE_MANAGER";
	
	private RedissonClient redisson;
		
	private CommandProcesser commandProcesser;
	
	private RTopic<Command> topic;
			
	public void init() {
		topic = redisson.getTopic(Contants.TOPIC_PREFIX_KEY + name);
		topic.addListener(new MessageListener<Command>() {

			public void onMessage(String channel, Command command) {
				commandProcesser.onProcess(command);
			}
		});
	}

	@Override
	public void onMessage(Command command) {
		commandProcesser.onProcess(command);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCommandProcesser(CommandTopicProcesser commandProcesser) {
		this.commandProcesser = commandProcesser;
	}

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}

	public void setCommandProcesser(CommandProcesser commandProcesser) {
		this.commandProcesser = commandProcesser;
	}
	
}
