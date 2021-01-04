package com.appleframework.cache.jedis.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.cache.jedis.config.RedisNode;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.Pool;

public class JedisSentinelFactory {

	private static Logger logger = LoggerFactory.getLogger(JedisSentinelFactory.class);

    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private String password;

    private String sentinelMaster;
    private List<RedisNode> redisServers;
    private int dbIndex = 0;

    private Pool<Jedis> pool;
    private JedisPoolConfig poolConfig = new JedisPoolConfig();

    /**
     * Constructs a new <code>JedisConnectionFactory</code> instance with default settings (default connection pooling, no
     * shard information).
     */
    public JedisSentinelFactory() {
    }

    /**
     * Returns a Jedis instance to be used as a Redis connection. The instance can be newly created or retrieved from a
     * pool.
     */
    protected Jedis fetchJedisConnector() {
        try {
        	return pool.getResource();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot get Jedis connection", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void init() {
       this.pool = createPool();
    }

    private Pool<Jedis> createPool() {
        if (StringUtils.isNotBlank(sentinelMaster)) {
            return createRedisSentinelPool();
        }
        else {
        	throw new IllegalArgumentException("the sentinelMaster can not be empty, please check your conf.");
        }
    }

    /**
     * Creates {@link JedisSentinelPool}.
     *
     * @return
     * @since 1.4
     */
    protected Pool<Jedis> createRedisSentinelPool() {
    	Set<String> hostAndPorts = new HashSet<String>();
    	for (RedisNode redisNode : redisServers) {
    		hostAndPorts.add(new HostAndPort(redisNode.host, redisNode.port).toString());
		}
        return new JedisSentinelPool(sentinelMaster, hostAndPorts, poolConfig, timeout, password);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() {
        if (pool != null) {
            try {
                pool.destroy();
            } catch (Exception ex) {
            	logger.warn("Cannot properly close Jedis pool", ex);
            }
            pool = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnectionFactory#getConnection()
     */
    public Jedis getJedisConnection() {
        Jedis jedis = fetchJedisConnector();
        if (dbIndex > 0 && jedis != null) {
            jedis.select(dbIndex);
        }
        return jedis;
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

    /**
     * Returns the index of the database.
     *
     * @return Returns the database index
     */
    public int getDatabase() {
        return dbIndex;
    }

    /**
     * Sets the index of the database used by this connection factory. Default is 0.
     *
     * @param index database index
     */
    public void setDatabase(int index) {
        this.dbIndex = index;
    }

    public void setSentinelMaster(String sentinelMaster) {
        this.sentinelMaster = sentinelMaster;
    }

    public void setRedisServers(List<RedisNode> redisServers) {
        if (redisServers == null || redisServers.isEmpty()) {
            throw new IllegalArgumentException("redis server node can not be empty, please check your conf.");
        }
        this.redisServers = redisServers;
    }
}
