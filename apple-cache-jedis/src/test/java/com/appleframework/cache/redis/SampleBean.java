package com.appleframework.cache.redis;

import java.io.Serializable;

public class SampleBean implements Serializable {

	private static final long serialVersionUID = -303232410998377570L;

	private String name;

	public SampleBean() {
	}

	public SampleBean(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}