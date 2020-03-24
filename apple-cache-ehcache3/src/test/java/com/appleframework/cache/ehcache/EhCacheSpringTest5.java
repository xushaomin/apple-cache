package com.appleframework.cache.ehcache;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/test-apple-cache-ehcache2.xml"})
public class EhCacheSpringTest5 {

	@Resource
	private CacheManager cacheManager;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 1; i < 100; i++) {
				cacheManager.set("" + i, User.create("" + i, i));
			}
			Thread.sleep(6000);
			for (int i = 1; i < 100; i++) {
				System.out.println(cacheManager.get(i+""));
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

