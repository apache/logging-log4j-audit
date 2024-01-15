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
package org.apache.logging.log4j.catalog.api.plugins;

import static org.apache.logging.log4j.catalog.api.util.StringUtils.appendNewline;
import static org.apache.logging.log4j.catalog.api.util.StringUtils.isBlank;

import org.apache.logging.log4j.catalog.api.ConstraintType;
import org.apache.logging.log4j.core.config.plugins.Plugin;

/**
 *
 */
@Plugin(name = "pattern", category = ConstraintType.CATEGORY)
public class PatternConstraint implements ConstraintType {

    @Override
    public void validate(boolean isRequestContext, String name, String value, String pattern, StringBuilder error) {
        if (!isBlank(pattern) && !isBlank(value)) {
            if (!value.matches(pattern)) {
                appendNewline(error);
                if (isRequestContext) {
                    error.append("ThreadContext key ");
                }
                error.append(name).append(" does not match pattern ").append(pattern);
            }
        }
    }
}
