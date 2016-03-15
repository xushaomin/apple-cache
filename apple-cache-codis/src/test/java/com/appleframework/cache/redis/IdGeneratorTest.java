package com.appleframework.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.codis.id.IdGenerator;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-codis-id.xml"})
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
	
	/** 
     * 多线程测试用例 
     *  
     */ 
    @Test 
    public void MultiRequestsTest() {
        
    	// 构造一个Runner 
        TestRunnable runner = new TestRunnable() {
            @Override
            public void runTest() throws Throwable {
                // 测试内容 
            	System.out.println(idGenerator.next("ddd", 100000000000L));
            } 
        }; 
        int runnerCount = 100;
        
        //Rnner数组，想当于并发多少个。 
        TestRunnable[] trs = new TestRunnable[runnerCount]; 
        for (int i = 0; i < runnerCount; i++) { 
            trs[i] = runner; 
        } 
        
        // 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入 
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs); 
        try { 
        	// 开发并发执行数组里定义的内容 
            mttr.runTestRunnables(); 
        } catch (Throwable e) { 
            e.printStackTrace(); 
        } 
    } 

}

