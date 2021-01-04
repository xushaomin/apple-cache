package com.appleframework.cache.ehcache;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/test-apple-cache-ehcache-spring2.xml"})
public class EhCacheSpringTest2 {

	@Resource
	private TestService testService;
	    
	@Test
	public void testAddOpinion1() {
		try {
			System.out.println(testService.getCache("xusm"));
			System.out.println(testService.getCache2("zyq"));
			
			for (int i = 1; i < 100; i++) {
				System.out.println("1-----------------------");
				System.out.println(testService.getCache("xusm"));
				Thread.sleep(6000);
				System.out.println(testService.getCache2("zyq"));
				System.out.println("2-----------------------");
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

