package com.appleframework.cache.jedis.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.jedis.config.RedisNode;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

public class JedisSentinelFactoryBean implements FactoryBean<JedisSentinelFactory> {

	private static Logger logger = LoggerFactory.getLogger(JedisSentinelFactoryBean.class);

	private JedisSentinelFactory factory = null;
	
	private String sentinelMaster = "";
	private String serverNodes;
	
	private JedisPoolConfig poolConfig;
	
	private boolean singleton = true;

	@Override
	public JedisSentinelFactory getObject() throws Exception {
		logger.info("begin init redis...");
		factory = new JedisSentinelFactory();
		factory.setRedisServers(getNodeList());
		factory.setSentinelMaster(sentinelMaster);
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
		return JedisSentinelFactory.class;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}
	
	public List<RedisNode> getNodeList() {
		List<String> nodeList = Arrays.asList(serverNodes.split(","));
		List<RedisNode> list = new ArrayList<RedisNode>();
		for (String node : nodeList) {
			RedisNode rn = new RedisNode();
			String[] rna = node.split(":");
			rn.setHost(rna[0]);
			rn.setPort(Integer.parseInt(rna[1]));
			list.add(rn);
		}
		return list;
	}
	
	public void destroy() {
		if (factory != null)
			factory.destroy();
	}
	
	public void setSentinelMaster(String sentinelMaster) {
		this.sentinelMaster = sentinelMaster;
	}

	public void setServerNodes(String serverNodes) {
		this.serverNodes = serverNodes;
	}

	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}
	
	public void setSingleton(Boolean singleton) {
		this.singleton = singleton;
	}
	
}
