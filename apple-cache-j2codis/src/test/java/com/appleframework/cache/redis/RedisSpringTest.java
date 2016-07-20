package com.appleframework.cache.redis;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-j2codis.xml" })
public class RedisSpringTest {

	@Resource
	private CacheManager j2CacheManager;

	@Test
	public void testAddOpinion1() {
		try {
			while (true) {
				Thread.sleep(10000);
				for (int i = 1; i < 100; i++) {
					System.out.println(j2CacheManager.get(String.valueOf(i)));
				}
				System.out.println("-------------------------------------"+ new Date());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
