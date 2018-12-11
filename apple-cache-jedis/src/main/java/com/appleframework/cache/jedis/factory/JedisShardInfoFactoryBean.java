package com.appleframework.cache.jedis.factory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisShardInfoFactoryBean implements FactoryBean<JedisShardInfoFactory> {

	private static Logger logger = Logger.getLogger(JedisShardInfoFactoryBean.class);

	private static JedisShardInfoFactory factory = null;

	private JedisPoolConfig poolConfig;

    private String host = "localhost";
    private int port = Protocol.DEFAULT_PORT;
    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private String password;
    private int database = 0;

	@Override
	public JedisShardInfoFactory getObject() throws Exception {
		if(null == factory) {
			logger.info("begin init redis...");
			factory = new JedisShardInfoFactory();
			factory.setPoolConfig(poolConfig);
			factory.setHostName(host);
			factory.setDatabase(database);
			factory.setPassword(password);
			factory.setPort(port);
			factory.setTimeout(timeout);
			factory.init();
			test();
		}
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

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

}
