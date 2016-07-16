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

import com.appleframework.cache.redis.ReadMode;
import com.appleframework.cache.redis.balancer.LoadBalancer;
import com.appleframework.cache.redis.balancer.RoundRobinLoadBalancer;

public class BaseMasterSlaveServersConfig extends BaseConfig {

    /**
     * Сonnection load balancer for multiple Redis slave servers
     */
    private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();


    private ReadMode readMode = ReadMode.SLAVE;

    public BaseMasterSlaveServersConfig() {
    }

    /**
     * Сonnection load balancer to multiple Redis slave servers.
     * Uses Round-robin algorithm by default
     *
     * @param loadBalancer
     * @return
     *
     * @see org.redisson.connection.balancer.RoundRobinLoadBalancer
     * @see org.redisson.connection.BaseLoadBalancer
     */
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * Set node type used for read operation.
     * <p/>
     * Default is <code>SLAVE</code>
     *
     * @param readMode
     * @return
     */
    public void setReadMode(ReadMode readMode) {
        this.readMode = readMode;
    }
    public ReadMode getReadMode() {
        return readMode;
    }

}
