package com.appleframework.cache.redisson;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.core.RSortedSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-redisson-master.xml"})
public class RedisSpringTest {

	@Resource
	private Redisson redisson;
	    
	@Test
	public void testAddOpinion1() {
		RSortedSet set = redisson.getSortedSet("dddd");
		
	}
	


}

