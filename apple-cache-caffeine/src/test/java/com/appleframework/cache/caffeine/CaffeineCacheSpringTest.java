package com.appleframework.cache.caffeine;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/test-apple-cache-caffeine.xml"})
public class CaffeineCacheSpringTest {

	@Resource
	private CacheManager cacheManager;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 1; i < 10; i++) {
				cacheManager.set("" + i, User.create("" + i, i), 5);
			}
			for (int i = 1; i < 10; i++) {
				System.out.println("1------->" + cacheManager.get(String.valueOf(i)));
			}
			Thread.sleep(10000);
			for (int i = 1; i < 10; i++) {
				System.out.println(cacheManager.get(String.valueOf(i)));
			}
			for (int i = 1; i < 10; i++) {
				cacheManager.remove(String.valueOf(i));
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

