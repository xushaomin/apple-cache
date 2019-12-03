package com.appleframework.cache.caffeine;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("testService")
public class TestService {
	
	private long time = System.currentTimeMillis();

	@Cacheable(value = "testd", key = "#name")
	public String getCache(String name) {
		System.out.println("-----service in------" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		return "hello " + name;
	}
}