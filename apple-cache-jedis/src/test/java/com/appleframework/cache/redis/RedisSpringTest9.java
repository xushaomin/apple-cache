package com.appleframework.cache.redis;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.jedis.factory.PoolFactory;

import redis.clients.jedis.Jedis;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-redis-manager3.xml" })
public class RedisSpringTest9 {

	@Resource
	private PoolFactory poolFactory;

	@SuppressWarnings("deprecation")
	@Test
	public void testAddOpinion1() {
		Jedis jedis = poolFactory.getWritePool().getResource();
		try {
			String key = "testddddddd";
			jedis.zadd(key, 100, "Java");
			Map<String, Double> scoreMembers = new HashMap<String, Double>();
			scoreMembers.put("Python", 90d);
			scoreMembers.put("Python90", 90d);
			scoreMembers.put("Javascript", 80d);
			jedis.zadd(key, scoreMembers);
			System.out.println("Number of Java users:" + jedis.zscore(key, "Java"));
			System.out.println("Number of elements:" + jedis.zcard(key));

			System.out.println("555555555:" + jedis.zrevrange(key, 0, 2));
		} catch (Exception e) {
		} finally {
			if (null != jedis) {
				poolFactory.getWritePool().returnResource(jedis);
			}
		}
	}

}
