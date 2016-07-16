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
package com.appleframework.cache.redis.balancer;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import redis.clients.jedis.JedisPool;

public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new SecureRandom();

    public JedisPool getJedisPool(List<JedisPool> jedisPoolsCopy) {
        int ind = random.nextInt(jedisPoolsCopy.size());
        return jedisPoolsCopy.get(ind);
    }

}
