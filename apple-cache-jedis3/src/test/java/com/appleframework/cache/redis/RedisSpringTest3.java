package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-redis-manager3.xml"})
public class RedisSpringTest3 {

	@Resource
	private CacheManager hsetCacheManager;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 0; i < 100; i++) {
				hsetCacheManager.set("" + i, User.create("" + i, i));
			}
			
			for (int i = 0; i < 100; i++) {
				System.out.println(hsetCacheManager.get("" + i, User.class));
			}
			
			System.out.println(hsetCacheManager.getList("1", "2", "3"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

