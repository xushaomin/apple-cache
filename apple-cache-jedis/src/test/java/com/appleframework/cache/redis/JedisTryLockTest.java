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
public class JedisTryLockTest {

	@Resource
	private PoolFactory poolFactory;

	int n = 500;

	@Test
	public void testAddOpinion1() {
		
		for (int i = 0; i < 50000; i++) {
			ThreadB threadB = new ThreadB(poolFactory);
			threadB.start();
			try {
				if(i % 5 == 0)
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class ThreadB extends Thread {

	public static int n = 500;

	String lockKey = "test_lock_7";

	private PoolFactory poolFactory;

	public ThreadB(PoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	@Override
	public void run() {
		Lock lock = new JedisLock(poolFactory, 999999000, 20000);
		if(!lock.tryLock(lockKey, 2000)) {
			return;
		}
		try {
			System.out.println(Thread.currentThread().getName() + "获得了锁");
			System.out.println(--n);
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock(lockKey);
			//System.out.println(Thread.currentThread().getName() + "解锁");
		}
	}
}
