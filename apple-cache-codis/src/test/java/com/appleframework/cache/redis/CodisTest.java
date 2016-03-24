package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.codis.CodisResourcePool;

import redis.clients.jedis.Jedis;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-codis.xml" })
public class CodisTest {

	@Resource
	private CodisResourcePool codisResourcePool;

	@Test
	public void testAddOpinion1() {
		try {
			for (Thread t : getThreads(codisResourcePool)) {
				t.start();
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Thread[] getThreads(final CodisResourcePool codisResourcePool) {
		Thread[] thread = new Thread[1000];
		for (int i = 0; i < 1000; i++) {
			thread[i] = new Thread(new Runnable() {
				public void run() {
					for (int j = 0; j < 1000; j++) {
						try (Jedis jedis = codisResourcePool.getResource()) {
							String key = ResourceKeyGenerator.getRandomNum(10);
							jedis.set(key, key);
						}
					}
				}
			});
		}
		return thread;
	}

}
