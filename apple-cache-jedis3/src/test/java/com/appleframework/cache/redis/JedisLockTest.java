package com.appleframework.cache.redis;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.core.lock.Lock;
import com.appleframework.cache.jedis.factory.PoolFactory;
import com.appleframework.cache.jedis.lock.JedisLock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-redis-manager3.xml" })
public class JedisLockTest {

	@Resource
	private PoolFactory poolFactory;

	int n = 500;

	@Test
	public void testAddOpinion1() {
		
		for (int i = 0; i < 50; i++) {
            ThreadA threadA = new ThreadA(poolFactory);
            threadA.start();
        }
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class ThreadA extends Thread {

	public static int n = 500;

	String lockKey = "test_lock";

	private PoolFactory poolFactory;

	public ThreadA(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	@Override
	public void run() {
		Lock lock = new JedisLock(poolFactory, 999999000, 20000);
		try {
			lock.lock(lockKey);
			System.out.println(Thread.currentThread().getName() + "获得了锁");
			System.out.println(--n);
			Thread.sleep(2000);
			System.out.println(Thread.currentThread().getName() + "解锁");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock(lockKey);
		}
	}
}
