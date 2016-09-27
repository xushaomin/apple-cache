package com.appleframework.cache.memcache;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.CacheManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/apple/apple-cache-hazelcast.xml"})
public class MemcacedSpringTest {

	@Resource
	private CacheManager cacheManager;
	    
	@Test
	public void testAddOpinion1() {
		try {			
			cacheManager.set("test", "---xusm");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

