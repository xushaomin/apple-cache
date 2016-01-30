package com.appleframework.cache.j2cache.topic;

import java.io.Serializable;

public class OperateObject implements Serializable {

	private static final long serialVersionUID = -4140447996333016680L;

	public enum OperateType {
		PUT, DELETE, CLEAR;
	}

	private OperateType operateType;
	private Object key;
	private Object value;

	public OperateType getOperateType() {
		return operateType;
	}

	public void setOperateType(OperateType operateType) {
		this.operateType = operateType;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
