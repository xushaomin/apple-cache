package com.appleframework.cache.redis;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.CacheManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:config/apple-cache-codis.xml"})
public class CodisSpringTest2 {

	@Resource
	private CodisResourcePool codisResourcePool;
	    
	@Test
	public void testAddOpinion1() {
		try (Jedis jedis = codisResourcePool.getResource()) {

			jedis.sadd("tom:friend:list", "123"); // tom的好友列表
			jedis.sadd("tom:friend:list", "456");
			jedis.sadd("tom:friend:list", "789");
			jedis.sadd("tom:friend:list", "101");

			jedis.set("uid:sort:123", "1000"); // 好友对应的成绩
			jedis.set("uid:sort:456", "6000");
			jedis.set("uid:sort:789", "100");
			jedis.set("uid:sort:101", "5999");
			
			
			jedis.hset("user", "id_1", "hanjie");
		    jedis.hset("user", "id_2", "hanjie");
		    jedis.hset("user", "id_3", "86");
		    jedis.hset("user", "id_4", "x86");

			SortingParams sortingParameters = new SortingParams();

			sortingParameters.desc();
			//sortingParameters.limit(0, 4);

			sortingParameters.get("user->id_*");
			//sortingParameters.get("uid:sort:*");
			//sortingParameters.by("uid:sort:*");
			
			List<String> result = jedis.sort("user-", sortingParameters);
			for(String item:result){
		            System.out.println("item..."+item);
		    }
			 
			/*List<String> results = jedis.sort("tom:friend:list", sortingParameters);
	        for(String item:results){
	            System.out.println("item..."+item);
	        }*/
			
		}
	}
	


}

