package com.appleframework.cache.redisson;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/test-apple-cache-redisson-master.xml"})
public class LockSpringTest {

	@Resource
	private Redisson redisson;
	    
	int n = 500;

	@Test
	public void testAddOpinion1() {
		
		for (int i = 0; i < 50; i++) {
            ThreadA threadA = new ThreadA(redisson);
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

	private Redisson redisson;

	public ThreadA(Redisson redisson) {
		this.redisson = redisson;
	}

	@Override
	public void run() {
		RLock lock = redisson.getLock(lockKey);
		try {
			
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获得了锁");
			System.out.println(--n);
			Thread.sleep(10000);
			System.out.println(Thread.currentThread().getName() + "解锁");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}

