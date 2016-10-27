package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-redisson4.xml"})
public class RedisSpringTest4 {

	@Resource
	private CacheManager cacheManager4;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 1000; i < 2000; i++) {
				cacheManager4.set("" + i, User.create("" + i, i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

