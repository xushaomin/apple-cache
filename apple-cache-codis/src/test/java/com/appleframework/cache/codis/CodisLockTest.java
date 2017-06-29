package com.appleframework.cache.codis;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.codis.lock.CodisLock;
import com.appleframework.cache.core.lock.Lock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/test-apple-cache-codis.xml" })
public class CodisLockTest {

	@Resource
	private CodisResourcePool codisResourcePool;

	int n = 500;

	@Test
	public void testAddOpinion1() {
		
		for (int i = 0; i < 50; i++) {
            ThreadA threadA = new ThreadA(codisResourcePool);
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

	private CodisResourcePool codisResourcePool;

	public ThreadA(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	@Override
	public void run() {
		Lock lock = new CodisLock(codisResourcePool, 999999000, 20000);
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
