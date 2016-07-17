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
package com.appleframework.cache.redis.misc;

import java.net.URI;

import org.springframework.util.StringUtils;

public class URIBuilder {

    public static URI create(String uri, int database, String password) {
        String[] parts = uri.split(":");
        if (parts.length-1 >= 3) {
            String port = parts[parts.length-1];
            uri = "[" + uri.replace(":" + port, "") + "]:" + port;
        }
        if(StringUtils.isEmpty(password)) {
            return URI.create("redis://" + uri + "/" + database);
        } else {
            return URI.create("redis://" + password + "@" + uri + "/" + database);
        }
    }
    
}
