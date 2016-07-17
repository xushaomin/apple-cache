package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-j2cache.xml"})
public class RedisSpringTest {

	@Resource
	private CacheManager j2CacheManager;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 600; i < 700; i++) {
				j2CacheManager.set("" + i, User.create("" + i, i));
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

