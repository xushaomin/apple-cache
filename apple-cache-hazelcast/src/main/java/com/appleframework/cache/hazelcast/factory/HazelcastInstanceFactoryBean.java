package com.appleframework.cache.hazelcast.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastInstanceFactoryBean implements FactoryBean<HazelcastInstance> {

	private List<String> memberList = new ArrayList<>();
	private String mancenterUrl;

	public void setMembers(String members) {
		String[] memberArray = members.split(",");
		for (String member : memberArray) {
			memberList.add(member);
		}
	}

	@Override
	public HazelcastInstance getObject() throws Exception {
		Config config = new Config();
		config.getNetworkConfig().setPortAutoIncrement(true);
		if(null != mancenterUrl) {
			ManagementCenterConfig manCenterConfig = new ManagementCenterConfig();
			manCenterConfig.setScriptingEnabled(true);
			config.setManagementCenterConfig(manCenterConfig);
		}
		NetworkConfig network = config.getNetworkConfig();
		JoinConfig join = network.getJoin();
		join.getMulticastConfig().setEnabled(false);
		join.getTcpIpConfig().setMembers(memberList).setEnabled(true);
		return Hazelcast.newHazelcastInstance(config);
	}

	@Override
	public Class<HazelcastInstance> getObjectType() {
		return HazelcastInstance.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setMancenterUrl(String mancenterUrl) {
		this.mancenterUrl = mancenterUrl;
	}

}
