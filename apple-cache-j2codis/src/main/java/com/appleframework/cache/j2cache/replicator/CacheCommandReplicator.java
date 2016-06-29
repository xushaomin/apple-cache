package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;

public class CacheCommandReplicator {
	
	private static Logger logger = Logger.getLogger(CacheCommandReplicator.class);
	
	private String name = "J2_CACHE_MANAGER";

	private JChannel channel;	
	
	public void setName(String name) {
		this.name = name;
	}

	public void init() {
		try {
			channel = new JChannel();
			channel.connect(name);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void destroy() {
		channel.close();
	}
	
	public void replicate(Command command) {
		try {
			logger.warn("send command: " + command);
			Message msg = new Message(null, null, command);
			channel.send(msg);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
