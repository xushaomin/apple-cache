package com.appleframework.cache.redis;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-redis-manager5.xml" })
public class RedisSpringTest10 {

	@Resource
	private CacheManager2 cacheManager2;

	@Test
	public void testAddOpinion1() {
		try {
			/*for (int i =1; i< 100; i++) {
				cacheManager.set("TEST:" + i, User.create("" + i, i));
			}*/
			List<Object> list = cacheManager2.getLists("TEST:*");
			for (Object object : list) {
				System.out.println(object);
			}
			/*list = cacheManager.getList(User.class, "TEST:1", "TEST:2", "TEST:3", "TEST:4", "TEST:5");
			for (User object : list) {
				System.out.println(object);
			}*/
			System.out.println("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
