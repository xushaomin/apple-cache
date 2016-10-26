package com.appleframework.cache.redis;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("testService")
public class TestService {

	@Cacheable(value = "testd", key = "#name")
	public String getCache(String name) {
		return "hello " + name;
	}
}
