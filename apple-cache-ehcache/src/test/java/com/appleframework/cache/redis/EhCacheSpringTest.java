package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-ehcache.xml"})
public class EhCacheSpringTest {

	@Resource
	private CacheManager cacheManager;
	    
	@Test
	public void testAddOpinion1() {
		try {
			/*for (int i = 1; i < 100; i++) {
				cacheManager.set("" + i, User.create("" + i, i));
			}*/
			for (int i = 1; i < 100; i++) {
				cacheManager.remove(String.valueOf(i));
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

