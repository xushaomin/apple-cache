package com.appleframework.cache.ehcache.enums;

public enum ExpiryType {

    TTI("time to idle"),
    TTL("time to leave");

	ExpiryType(String desc) {
		this.desc = desc;
	}
	
	private String desc;

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
