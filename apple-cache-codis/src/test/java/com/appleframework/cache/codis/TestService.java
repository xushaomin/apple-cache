package com.appleframework.cache.codis;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("testService")
public class TestService {

	@Cacheable(value = "testd", key = "#name")
	public String getCache(String name) {
		System.out.println("-----service in------" + System.currentTimeMillis());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		return "hello " + name;
	}
}
