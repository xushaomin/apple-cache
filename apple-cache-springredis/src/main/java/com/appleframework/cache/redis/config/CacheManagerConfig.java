package com.appleframework.cache.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.appleframework.cache.core.CacheManager;
import com.appleframework.cache.redis.manager.RedisBucketCacheManager;
import com.appleframework.cache.redis.manager.RedisHmsetCacheManager;

@Configuration
public class CacheManagerConfig {

    @Bean(name = "redisBucketCacheManager")
    public CacheManager redisBucketCacheManagerFactory(RedisTemplate<String, Object> redisTemplate) {
    	RedisBucketCacheManager cacheManager = new RedisBucketCacheManager();
    	cacheManager.setRedisTemplate(redisTemplate);
        return cacheManager;
    }

    @Bean(name = "redisHmsetCacheManager")
    public CacheManager redisHmsetCacheManagerFactory(RedisTemplate<String, Object> redisTemplate) {
    	RedisHmsetCacheManager cacheManager = new RedisHmsetCacheManager();
    	cacheManager.setRedisTemplate(redisTemplate);
        return cacheManager;
    }

}