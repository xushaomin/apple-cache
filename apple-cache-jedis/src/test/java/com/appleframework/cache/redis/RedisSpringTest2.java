package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-redis-manager2.xml"})
public class RedisSpringTest2 {

	@Resource
	private CacheManager redisCacheManager2;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 1000; i < 2000; i++) {
				redisCacheManager2.set("" + i, User.create("" + i, i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

