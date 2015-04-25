package com.appleframework.cache.hazelcast;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/apple/apple-cache-hazelcast.xml"})
public class HazelcastSpringTest {

	@Resource
	private HazelcastInstance hazelcastInstance;
	    
	@Test
	public void testAddOpinion1() {
		try {
			Map<Integer, String> map = hazelcastInstance.getMap("test");
			for (int i = 0; i < 100; i++) {
				map.put(i, i + "-->");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

