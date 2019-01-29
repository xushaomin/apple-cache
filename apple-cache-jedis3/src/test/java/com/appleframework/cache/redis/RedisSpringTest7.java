package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-redis-manager3.xml" })
public class RedisSpringTest7 {

	@Resource
	private CacheManager cacheManager;

	@Test
	public void testAddOpinion1() {
		try {
			int i = 0;
			while (true) {
				try {
					String key = "xxxx:" + i;
					cacheManager.set(key, key); // tom的好友列表
					System.out.println(cacheManager.get(key));
					Thread.sleep(5000);
					i++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
