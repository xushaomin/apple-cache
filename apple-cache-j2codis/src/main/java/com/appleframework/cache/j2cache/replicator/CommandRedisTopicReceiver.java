package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.CommandReceiver;
import com.appleframework.cache.j2cache.utils.SerializeUtility;
import com.appleframework.cache.redis.factory.PoolFactory;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class CommandRedisTopicReceiver extends BinaryJedisPubSub implements CommandReceiver {
	
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
	
	@Override
	// 取得订阅的消息后的处理   
	public void onMessage(byte[] channel, byte[] message) {
		Object omessage = SerializeUtility.unserialize(message);
		Object ochannel = SerializeUtility.unserialize(channel);
		try {
	        logger.info("取得订阅的消息后的处理 : " + ochannel + "=" + omessage.toString());  
			if (omessage instanceof Command) {
				Command command = (Command)omessage;
				this.onMessage(command);
			} else if (omessage instanceof String) {
				logger.warn(omessage.toString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@Override
    // 初始化订阅时候的处理    
    public void onSubscribe(byte[] channel, int subscribedChannels) {  
        logger.info("初始化订阅时候的处理 : " + channel + "=" + subscribedChannels);  
    }  
  
	@Override
    // 取消订阅时候的处理    
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {  
        logger.info("取消订阅时候的处理 : " + channel + "=" + subscribedChannels);  
    }  
  
	@Override
    // 初始化按表达式的方式订阅时候的处理    
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {  
        logger.info("初始化按表达式的方式订阅时候的处理 : " + pattern + "=" + subscribedChannels);  
    }  
  
	@Override
    // 取消按表达式的方式订阅时候的处理    
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {  
        logger.info(" 取消按表达式的方式订阅时候的处理 : " + pattern + "=" + subscribedChannels);  
    }  
  
	@Override
    // 取得按表达式的方式订阅的消息后的处理    
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {  
        logger.info("取得按表达式的方式订阅的消息后的处理 :" + pattern + "=" + channel + "=" + message);  
    }  


	public void setPoolFactory(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	public void setCommandProcesser(CommandRedisTopicProcesser commandProcesser) {
		this.commandProcesser = commandProcesser;
	}
}
