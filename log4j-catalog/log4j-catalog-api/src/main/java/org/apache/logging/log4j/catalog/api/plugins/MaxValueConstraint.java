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

import java.math.BigDecimal;

import org.apache.logging.log4j.catalog.api.ConstraintType;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import static org.apache.logging.log4j.catalog.api.util.StringUtils.appendNewline;
import static org.apache.logging.log4j.catalog.api.util.StringUtils.isBlank;

/**
 *
 */
@Plugin(name = "maxValue", category = ConstraintType.CATEGORY)
public class MaxValueConstraint implements ConstraintType {

    @Override
    public void validate(boolean isRequestContext, String name, String value, String maxValue, StringBuilder error) {
        if (isBlank(maxValue)) {
            appendNewline(error);
            if (isRequestContext) {
                error.append("ThreadContext key ");
            }
            error.append(name).append(" has no value for the minimum value defined");
            return;
        }
        if (!isBlank(value)) {
            try {
                BigDecimal minVal = new BigDecimal(maxValue);
                BigDecimal val = new BigDecimal(value);
                if (val.compareTo(minVal) > 0) {
                    appendNewline(error);
                    if (isRequestContext) {
                        error.append("ThreadContext key ");
                    }
                    error.append(name).append(" is less than ").append(maxValue);
                }
            } catch (Exception ex) {
                appendNewline(error);
                if (isRequestContext) {
                    error.append("ThreadContext key ");
                }
                error.append(name).append(" encountered an error trying to determine the minimum value: ").append(ex.getMessage());
            }
        }
    }
}
