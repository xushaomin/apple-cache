package com.appleframework.cache.j2cache.replicator;

import org.apache.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.appleframework.cache.core.replicator.Command;
import com.appleframework.cache.core.replicator.CommandProcesser;
import com.appleframework.cache.core.replicator.CommandReceiver;

public class CommandMulticastReceiver extends ReceiverAdapter implements CommandReceiver {

	protected final static Logger logger = Logger.getLogger(CommandMulticastReceiver.class);

	private String name = "J2_CACHE_MANAGER";

	private JChannel channel;

	private CommandProcesser commandProcesser;

	public void init() {
		try {
			channel = new JChannel();
			channel.setReceiver(this);
			channel.connect(name);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}

	@Override
	public void onMessage(Command command) {
		commandProcesser.onProcess(command);
	}

	@Override
	public void receive(Message msg) {
		try {
			Object object = msg.getObject();
			if (object instanceof Command) {
				Command command = (Command) object;
				this.onMessage(command);
			} else if (object instanceof String) {
				logger.warn(object.toString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void viewAccepted(View new_view) {
		logger.warn("** view: " + new_view);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void destroy() {
		channel.close();
	}

}
