package com.appleframework.cache.jedis.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.jedis.config.RedisNode;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisClusterFactory {

	private static Logger logger = LoggerFactory.getLogger(JedisClusterFactory.class);

    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private String password;

    private List<RedisNode> redisServers;
    
    private Integer maxAttempts = 10;

    private JedisCluster cluster;
    private JedisPoolConfig poolConfig = new JedisPoolConfig();

    /**
     * Constructs a new <code>JedisConnectionFactory</code> instance with default settings (default connection pooling, no
     * shard information).
     */
    public JedisClusterFactory() {
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void init() {
        this.cluster = createCluster();
    }

    /**
     * @return
     * @since 1.7
     */
    protected JedisCluster createCluster() {
    	Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
    	for (RedisNode redisNode : redisServers) {
    		hostAndPorts.add(new HostAndPort(redisNode.host, redisNode.port));
		}
        if (StringUtils.isNotEmpty(getPassword())) {
            throw new IllegalArgumentException("Jedis does not support password protected Redis Cluster configurations!");
        }
        if (StringUtils.isNotEmpty(password)) {
            return new JedisCluster(hostAndPorts, timeout, timeout, maxAttempts, password, poolConfig);
        }
        else {
            return new JedisCluster(hostAndPorts, timeout, maxAttempts, poolConfig);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() {
        if (cluster != null) {
            try {
                cluster.close();
            } catch (Exception ex) {
            	logger.warn("Cannot properly close Jedis cluster", ex);
            }
            cluster = null;
        }
    }

    public JedisCluster getClusterConnection() {
        return cluster;
    }

    /**
     * Returns the password used for authenticating with the Redis server.
     *
     * @return password for authentication
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password used for authenticating with the Redis server.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Returns the timeout.
     *
     * @return Returns the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the poolConfig.
     *
     * @return Returns the poolConfig
     */
    public JedisPoolConfig getPoolConfig() {
        return poolConfig;
    }

    /**
     * Sets the pool configuration for this factory.
     *
     * @param poolConfig The poolConfig to set.
     */
    public void setPoolConfig(JedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }
    
    public void setRedisServers(List<RedisNode> redisServers) {
        if (redisServers == null || redisServers.isEmpty()) {
            throw new IllegalArgumentException("redis server node can not be empty, please check your conf.");
        }
        this.redisServers = redisServers;
    }

	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
    
}
