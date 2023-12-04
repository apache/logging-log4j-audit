/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 *
 */
public class JdbcUrlCondition implements Condition {

    private static final Logger LOGGER = LogManager.getLogger(JdbcUrlCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        Map<String, Object> map = metadata.getAnnotationAttributes(JdbcUrl.class.getName());
        if (map != null && map.containsKey("value")) {
            String value = map.get("value").toString();
            String jdbcUrl = env.getProperty("jdbcUrl");
            boolean isEmbedded = Boolean.parseBoolean(env.getProperty("isEmbedded"));
            boolean result;
            if (value.equals("hsqldb")) {
                result = jdbcUrl == null || isEmbedded;
            } else if (jdbcUrl == null || isEmbedded) {
                result = false;
            } else if (!jdbcUrl.startsWith("jdbc:")) {
                result = false;
            } else {
                result = jdbcUrl.substring(5).toLowerCase().startsWith(value.toLowerCase());
            }
            LOGGER.debug("Returning {} for {}", result, value);
            return result;
        }
        LOGGER.debug("No data provided");
        return false;
    }
}
