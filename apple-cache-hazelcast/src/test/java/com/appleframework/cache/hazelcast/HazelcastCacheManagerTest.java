package com.appleframework.cache.hazelcast;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/apple/apple-cache-hazelcast.xml"})
public class HazelcastCacheManagerTest {

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

