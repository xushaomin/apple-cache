/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appleframework.cache.redis.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MasterSlaveServersConfig extends BaseMasterSlaveServersConfig {

	/**
	 * Redis slave servers addresses
	 */
	private Set<String> slaveAddresses = new HashSet<String>();

	/**
	 * Redis master server address
	 */
	private List<String> masterAddress;

	/**
	 * Database index used for Redis connection
	 */
	private int database = 0;

	public MasterSlaveServersConfig() {
	}

	public MasterSlaveServersConfig(MasterSlaveServersConfig config) {
		setLoadBalancer(config.getLoadBalancer());
		setMasterAddress(config.getMasterAddress());
		setSlaveAddresses(config.getSlaveAddresses());
		setDatabase(config.getDatabase());
	}

	public void setMasterAddressUri(String masterAddressUri) {
		if (null == masterAddress)
			masterAddress = new ArrayList<String>();
		masterAddress.add(masterAddressUri);
	}

	public void setMasterAddressUris(String masterAddressUris) {
		String[] masterAddressUriss = masterAddressUris.split(",");
		for (String address : masterAddressUriss) {
			masterAddress.add(address);
		}
	}

	public void setSlaveAddressUris(String slaveAddressUris) {
		String[] slaveAddressesUriss = slaveAddressUris.split(",");
		for (String address : slaveAddressesUriss) {
			slaveAddresses.add(address);
		}
	}

	/**
	 * Set Redis master server address. Use follow format -- host:port
	 *
	 * @param masterAddress
	 */
	public MasterSlaveServersConfig setMasterAddress(String masterAddress) {
		if (masterAddress != null) {
			this.masterAddress = Collections.singletonList(masterAddress);
		}
		return this;
	}

	public String getMasterAddress() {
		if (masterAddress != null) {
			return masterAddress.get(0);
		}
		return null;
	}

	/**
	 * Add Redis slave server address. Use follow format -- host:port
	 *
	 * @param addresses
	 * @return
	 */
	public MasterSlaveServersConfig addSlaveAddress(String... sAddresses) {
		for (String address : sAddresses) {
			slaveAddresses.add(address);
		}
		return this;
	}

	public MasterSlaveServersConfig addSlaveAddress(String slaveAddress) {
		slaveAddresses.add(slaveAddress);
		return this;
	}

	public Set<String> getSlaveAddresses() {
		return slaveAddresses;
	}

	public void setSlaveAddresses(Set<String> readAddresses) {
		this.slaveAddresses = readAddresses;
	}

	/**
	 * Database index used for Redis connection Default is <code>0</code>
	 *
	 * @param database
	 */
	public MasterSlaveServersConfig setDatabase(int database) {
		this.database = database;
		return this;
	}

	public int getDatabase() {
		return database;
	}

}
