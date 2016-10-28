package com.appleframework.cache.codis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/test-apple-cache-codis.xml"})
public class CodisSpringTest6 {

	@Resource
	private CacheManager cacheManager2;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 0; i < 100; i++) {
				cacheManager2.set("" + i, User.create("" + i, i));
			}
			
			for (int i = 0; i < 100; i++) {
				System.out.println(cacheManager2.get("" + i, User.class));
			}
			
			System.out.println(cacheManager2.getList("1", "2", "3"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

