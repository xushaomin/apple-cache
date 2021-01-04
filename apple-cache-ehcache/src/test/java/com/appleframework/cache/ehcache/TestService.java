package com.appleframework.cache.ehcache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("testService")
public class TestService {
	
	private long time = System.currentTimeMillis();

	@Cacheable(value = "testd", key = "#name")
	public String getCache(String name) {
		System.out.println("-----testd service in------" + name + "--->" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		return "hello " + name;
	}
	
	@Cacheable(value = "teste", key = "#name")
	public String getCache2(String name) {
		System.out.println("-----teste service in------" + name + "--->" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		return "hello " + name;
	}
}