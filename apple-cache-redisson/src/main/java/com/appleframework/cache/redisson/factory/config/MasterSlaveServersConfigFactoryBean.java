package com.appleframework.cache.redisson.factory.config;

import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.springframework.beans.factory.FactoryBean;

public class MasterSlaveServersConfigFactoryBean implements FactoryBean<Config> {
	
    /**
     * Redis 'slave' node minimum idle connection amount for <b>each</b> slave node
     */
    private int slaveConnectionMinimumIdleSize = 10;

    /**
     * Redis 'slave' node maximum connection pool size for <b>each</b> slave node
     */
    private int slaveConnectionPoolSize = 64;

    /**
     * Redis 'master' node minimum idle connection amount for <b>each</b> slave node
     */
    private int masterConnectionMinimumIdleSize = 10;

    /**
     * Redis 'master' node maximum connection pool size
     */
    private int masterConnectionPoolSize = 64;
    
    /**
     * Database index used for Redis connection
     */
    private int database = 0;
    
    /**
     * If pooled connection not used for a <code>timeout</code> time
     * and current connections amount bigger than minimum idle connections pool size,
     * then it will closed and removed from pool.
     * Value in milliseconds.
     *
     */
    private int idleConnectionTimeout = 10000;

    /**
     * Ping timeout used in <code>Node.ping</code> and <code>Node.pingAll<code> operation.
     * Value in milliseconds.
     *
     */
    private int pingConnectionInterval = 1000;

    /**
     * Timeout during connecting to any Redis server.
     * Value in milliseconds.
     *
     */
    private int connectTimeout = 10000;

    /**
     * Redis server response timeout. Starts to countdown when Redis command was succesfully sent.
     * Value in milliseconds.
     *
     */
    private int timeout = 3000;

    private int retryAttempts = 3;

    private int retryInterval = 1500;

    /**
     * Password for Redis authentication. Should be null if not needed
     */
    private String password;

    /**
     * Subscriptions per Redis connection limit
     */
    private int subscriptionsPerConnection = 5;

    /**
     * Name of client connection
     */
    private String clientName;
    
    /**
     * Redis key/value codec. JsonJacksonCodec used by default
     */
    private Codec codec;

	private String masterAddress;
	
	private String[] slaveAddresses;
		
	public void setSlaveAddresses(String slaveAddresses) {
		this.slaveAddresses = slaveAddresses.split(",");
	}

	@Override
	public Config getObject() throws Exception {
		Config config = new Config();
		config.useMasterSlaveServers()
			.setMasterAddress(masterAddress)
			.addSlaveAddress(slaveAddresses)
			.setDatabase(database)
			.setConnectTimeout(connectTimeout)
			.setIdleConnectionTimeout(idleConnectionTimeout)
			.setPingConnectionInterval(pingConnectionInterval)
			.setSlaveConnectionMinimumIdleSize(slaveConnectionMinimumIdleSize)
			.setSlaveConnectionPoolSize(slaveConnectionPoolSize)
			.setSubscriptionsPerConnection(subscriptionsPerConnection)
			.setTimeout(timeout)
			.setMasterConnectionMinimumIdleSize(masterConnectionMinimumIdleSize)
			.setMasterConnectionPoolSize(masterConnectionPoolSize)
			.setRetryAttempts(retryAttempts)
			.setRetryInterval(retryInterval)
			.setPassword(password)
			.setClientName(clientName);
		if(null != codec) {
			config.setCodec(codec);
		}
		return config;
	}

	@Override
	public Class<Config> getObjectType() {
		return Config.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setMasterAddress(String masterAddress) {
		this.masterAddress = masterAddress;
	}

	public void setSlaveConnectionMinimumIdleSize(int slaveConnectionMinimumIdleSize) {
		this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
	}

	public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
		this.slaveConnectionPoolSize = slaveConnectionPoolSize;
	}

	public void setMasterConnectionMinimumIdleSize(int masterConnectionMinimumIdleSize) {
		this.masterConnectionMinimumIdleSize = masterConnectionMinimumIdleSize;
	}

	public void setMasterConnectionPoolSize(int masterConnectionPoolSize) {
		this.masterConnectionPoolSize = masterConnectionPoolSize;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public void setIdleConnectionTimeout(int idleConnectionTimeout) {
		this.idleConnectionTimeout = idleConnectionTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setRetryAttempts(int retryAttempts) {
		this.retryAttempts = retryAttempts;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setSubscriptionsPerConnection(int subscriptionsPerConnection) {
		this.subscriptionsPerConnection = subscriptionsPerConnection;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setCodec(Codec codec) {
		this.codec = codec;
	}

	public void setPingConnectionInterval(int pingConnectionInterval) {
		this.pingConnectionInterval = pingConnectionInterval;
	}
	
}
