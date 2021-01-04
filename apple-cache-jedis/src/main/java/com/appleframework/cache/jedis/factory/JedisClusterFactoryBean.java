package com.appleframework.cache.jedis.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.appleframework.cache.jedis.config.RedisNode;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClusterFactoryBean implements FactoryBean<JedisClusterFactory> {

	private static Logger logger = LoggerFactory.getLogger(JedisClusterFactoryBean.class);

	private JedisClusterFactory factory = null;

	private String serverNodes;
	private String password;

	private JedisPoolConfig poolConfig;
	
	private boolean singleton = true;

	@Override
	public JedisClusterFactory getObject() throws Exception {
		logger.info("begin init redis...");
		factory = new JedisClusterFactory();
		factory.setRedisServers(getNodeList());
		factory.setPoolConfig(poolConfig);
		if(StringUtils.isNotEmpty(password)) {
			factory.setPassword(password);
		}
		factory.init();
		test();
		logger.info("test the redis...");
		return factory;
	}

	public void test() {
		JedisCluster cluster = factory.getClusterConnection();
		if (cluster == null)
			throw new RuntimeException("init redis cluster error.");
	}

	@Override
	public Class<?> getObjectType() {
		return JedisClusterFactory.class;
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

	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

	public void setServerNodes(String serverNodes) {
		this.serverNodes = serverNodes;
	}

	public void setSingleton(Boolean singleton) {
		this.singleton = singleton;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}