package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/test-apple-cache-ehcache-spring2.xml"})
public class EhCacheSpringTest3 {

	@Resource
	private TestService testService;
	
	@Resource
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
		
	@Test
	public void testAddOpinion1() {
		try {
			for (int i = 1; i < 100000; i++) {
				
				try {
					
					// 使用多线程处理 更新缓存
					threadPoolTaskExecutor.execute(new Runnable() {
						public void run() {
							testService.getCache("xusm");
						}
					});
					
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			
			for (int i = 1; i < 100000; i++) {
				
				try {
					Thread.sleep(100);
					// 使用多线程处理 更新缓存
					threadPoolTaskExecutor.execute(new Runnable() {
						public void run() {
							testService.getCache("xusm");
						}
					});
					
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}

