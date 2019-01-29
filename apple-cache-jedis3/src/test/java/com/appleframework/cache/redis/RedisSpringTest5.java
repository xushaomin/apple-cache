package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-redisson4.xml"})
public class RedisSpringTest5 {

	@Resource
	private CacheManager cacheManager4;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 1000; i < 2000; i++) {
				User user = cacheManager4.get("" + i, User.class);
				System.out.println(user.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

