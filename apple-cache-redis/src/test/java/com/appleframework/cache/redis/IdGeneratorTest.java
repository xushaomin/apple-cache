package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.redis.id.IdGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-redis-id.xml"})
public class IdGeneratorTest {

	@Resource
	private IdGenerator idGenerator;
	    
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 0; i < 1000; i++) {
				System.out.println(idGenerator.next("ddd", 100000000000L));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

