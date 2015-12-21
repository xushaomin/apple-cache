package com.appleframework.cache.redis;

import java.util.Iterator;

import org.junit.Test;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RDeque;
import org.redisson.core.RList;
import org.redisson.core.RQueue;
import org.redisson.core.RSet;

public class RedissonTest {

	@Test
    public void testRedisson() {
		
		try {
			Config config = new Config();
	        config.useSingleServer().setAddress("112.74.209.109:6379");
	        

	        RedissonClient redisson = Redisson.create(config);

	        // Set测试
	        RSet<String> mySet = redisson.getSet("mySet");
	        if (mySet != null) {
	            mySet.clear();
	        }
	        mySet.add("1");
	        mySet.add("2");
	        mySet.add("3");

	        RSet<String> mySetCache = redisson.getSet("mySet");

	        for (String s : mySetCache) {
	            System.out.println(s);
	        }

	        System.out.println("--------------------");

	        // List测试
	        RList<SampleBean> myList = redisson.getList("myList");
	        if (myList != null) {
	            myList.clear();
	        }    
	        
	        myList.add(new SampleBean("A"));
	        myList.add(new SampleBean("B"));
	        myList.add(new SampleBean("C"));

	        RList<SampleBean> myListCache = redisson.getList("myList");

	        for (SampleBean bean : myListCache) {
	            System.out.println(bean);
	        }
	        
	        System.out.println("--------------------");
	        
	        //Queue测试
	        RQueue<String> myQueue = redisson.getQueue("myQueue");
	        if (myQueue != null) {
	            myQueue.clear();
	        }
	        myQueue.add("X");
	        myQueue.add("Y");
	        myQueue.add("Z");
	        
	        RQueue<String> myQueueCache = redisson.getQueue("myQueue");

	        for (String s : myQueueCache) {
	            System.out.println(s);
	        }
	        
	        System.out.println("--------------------");
	        
	        System.out.println(myQueue.size());//3
	        System.out.println(myQueue.poll());//X
	        System.out.println(myQueue.size());//2
	        
	        System.out.println("--------------------");
	        
	        //注：虽然是从myQueue中poll的，但是由于myQueueCache与myQueue实际上是同一个缓存对象，所以下面的循环，也只剩2项
	        for (String s : myQueueCache) {
	            System.out.println(s);
	        }        
	        System.out.println("--------------------");
	        
	        //Deque测试
	        RDeque<String> myDeque = redisson.getDeque("myDeque");
	        if (myDeque != null) {
	            myDeque.clear();
	        }
	        myDeque.add("A");
	        myDeque.add("B");
	        myDeque.add("C");
	        
	        RDeque<String> myDequeCache = redisson.getDeque("myDeque");

	        Iterator<String> descendingIterator = myDequeCache.descendingIterator();
	        
	        //倒序输出
	        while (descendingIterator.hasNext()) {
	            System.out.println(descendingIterator.next());
	            
	        }
	        
	        redisson.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }


}
