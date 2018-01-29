/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.catalog.api.annotation;

import java.util.Map;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 *
 */
public class JdbcUrlCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        Map<String, Object> map = metadata.getAnnotationAttributes(JdbcUrl.class.getName());
        if (map != null && map.containsKey("value")) {
            String value = map.get("value").toString();
            String jdbcUrl = env.getProperty("jdbcUrl");
            Boolean isEmbedded = Boolean.parseBoolean(env.getProperty("isEmbedded"));
            if (value.equals("hsqldb")) {
                return jdbcUrl == null || isEmbedded;
            }
            if (jdbcUrl == null) {
                return false;
            }
            if (!jdbcUrl.startsWith("jdbc:")) {
                return false;
            }
            boolean result = jdbcUrl.substring(5).toLowerCase().startsWith(value.toLowerCase());
            return result;
        }
        return false;
    }
}
