package com.appleframework.cache.ehcache;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/test-apple-cache-ehcache.xml"})
public class EhCacheSpringTest {

	@Resource
	private CacheManager cacheManager;
	    
	@Test
	public void testAddOpinion1() {
		try {
			int count = 200;
			for (int i = 1; i <= count; i++) {
				cacheManager.set("" + i, User.create("" + i, i));
			}
			//Thread.sleep(3000);
			//for (int i = 1; i <= count; i++) {
			//	cacheManager.remove(String.valueOf(i));
			//}
			System.out.println("ok");
			//EhCacheExpiryUtil.setExpiry("1000", 111);
			System.out.println("-------1" + cacheManager.get(20 + ""));
			Thread.sleep(12000);
			
			System.out.println("-------2" + cacheManager.get(20 + ""));
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

