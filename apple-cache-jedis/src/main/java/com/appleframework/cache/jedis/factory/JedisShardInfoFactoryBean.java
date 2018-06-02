package com.appleframework.cache.jedis.factory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

public class JedisShardInfoFactoryBean implements FactoryBean<JedisShardInfoFactory> {

	private static Logger logger = Logger.getLogger(JedisShardInfoFactoryBean.class);

	private final JedisShardInfoFactory factory = new JedisShardInfoFactory();

	private JedisPoolConfig poolConfig;

	@Override
	public JedisShardInfoFactory getObject() throws Exception {
		logger.info("begin init redis...");
		factory.setPoolConfig(poolConfig);
		factory.init();
		test();
		return factory;
	}

	public void test() {
		Jedis jedis = factory.getJedisConnection();
		if (jedis == null)
			throw new RuntimeException("init redis error, can not get connection.");
		jedis.close();

	}

	@Override
	public Class<?> getObjectType() {
		return JedisShardInfoFactory.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void destroy() {
		if (factory != null)
			factory.destroy();
	}

	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

}
