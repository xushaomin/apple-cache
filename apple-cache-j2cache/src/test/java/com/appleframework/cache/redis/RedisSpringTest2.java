package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-j2cache.xml" })
public class RedisSpringTest2 {
	
	@Resource
	private CacheManager j2CacheManager;

	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 1; i < 100; i++) {
				j2CacheManager.set(String.valueOf(i), User.create(String.valueOf(i), i));
			}
			for (int i = 1; i < 100; i++) {
				System.out.println(j2CacheManager.get(String.valueOf(i)));
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
