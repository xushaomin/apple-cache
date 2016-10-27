package com.appleframework.cache.redis;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.core.utils.SerializeUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-codis.xml"})
public class CodisSpringTest3 {

	@Resource
	private CodisResourcePool codisResourcePool;
	    
	@Test
	public void testAddOpinion1() {
		try (Jedis jedis = codisResourcePool.getResource()) {
			
			jedis.hset("test2", "t1", new String("1"));
			jedis.hset("test2", "t2", new String("2"));
			jedis.hset("test2", "t3", new String("3"));
			jedis.hset("test2", "t4", new String("4"));

			//jedis.hincrBy("test".getBytes(), "test2222".getBytes(), 1000L);
			
			
			Map<String, String> value = jedis.hgetAll("test2");
			
			System.out.println(value);
			
			//System.out.println(bytesToLong(value));
		}
	}
	
	private static ByteBuffer buffer = ByteBuffer.allocate(8);   

	public static long bytesToLong(byte[] bytes) {  
	       buffer.put(bytes, 0, bytes.length);  
	       buffer.flip();//need flip   
	       return buffer.getLong();  
	   } 


}

