package com.appleframework.cache.core.replicator;

import java.io.Serializable;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum CommandType {
		PUT, DELETE, CLEAR;
	}

	private CommandType type;
	private Object key;
	private Integer timeout = 0;

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}
	
	public CommandType getType() {
		return type;
	}

	public void setType(CommandType type) {
		this.type = type;
	}

	public Command() {}

	public Command(CommandType commandType, Object key) {
		super();
		this.type = commandType;
		this.key = key;
	}
	
	public Command(CommandType commandType, Object key, Integer timeout) {
		super();
		this.type = commandType;
		this.key = key;
		this.timeout = timeout;
	}
	
	public Command(CommandType commandType) {
		super();
		this.type = commandType;
	}
	
	public static Command create(CommandType commandType, Object key, Integer timeout) {
		return new Command(commandType, key, timeout);
	}
	
	public static Command create(CommandType commandType, Object key) {
		return new Command(commandType, key);
	}
	
	public static Command create(CommandType commandType) {
		return new Command(commandType);
	}
	
	public static Command create() {
		return new Command();
	}

	@Override
	public String toString() {
		return "Command [type=" + type + ", key=" + key + ", timeout=" + timeout + "]";
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getTimeout() {
		return timeout;
	}

}
