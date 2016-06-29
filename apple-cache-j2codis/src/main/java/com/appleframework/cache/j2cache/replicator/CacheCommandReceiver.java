package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.appleframework.cache.j2cache.replicator.Command.CommandType;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class CacheCommandReceiver extends ReceiverAdapter {
	
	protected final static Logger logger = Logger.getLogger(CacheCommandReceiver.class);
	
	private String name = "J2_CACHE_MANAGER";
	
	private JChannel channel;
	
	private CacheManager ehcacheManager;

	public void start() throws Exception {
		// 创建一个通道
		channel = new JChannel();
		// 创建一个接收器
		channel.setReceiver(this);
		// 加入一个群
		channel.connect(name);
	}

	// 覆盖父类的方法
	@Override
	public void receive(Message msg) {
		Object object = msg.getObject();
		if (object instanceof Command) {
			
			Command command = (Command)object;
			Object key = command.getKey();
			if(command.getType().equals(CommandType.CLEAR)) {
				this.getEhCache().removeAll();
			}
			else if(command.getType().equals(CommandType.PUT)) {
				this.getEhCache().remove(key);
			}
			else if(command.getType().equals(CommandType.DELETE)) {
				this.getEhCache().remove(key);
			}
			else {
				logger.warn(command.getType().name());
			}
		} else if (object instanceof String) {
			logger.warn(object.toString());
		}
	}

	@Override
	public void viewAccepted(View new_view) {
		logger.warn("** view: " + new_view);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}
	
	public Cache getEhCache() {
		Cache cache = ehcacheManager.getCache(name);
		if (null == cache) {
			ehcacheManager.addCache(name);
			return ehcacheManager.getCache(name);
		} else {
			return cache;
		}
	}
	
}
