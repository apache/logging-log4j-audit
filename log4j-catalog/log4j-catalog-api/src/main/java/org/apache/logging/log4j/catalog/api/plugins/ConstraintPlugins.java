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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.ConstraintType;
import org.apache.logging.log4j.catalog.api.exception.ConstraintCreationException;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.util.ReflectionUtil;

/**
 *
 */
public class ConstraintPlugins {

    private static final Logger LOGGER = LogManager.getLogger(ConstraintPlugins.class);

    private static final Map<String, ConstraintType> constraintMap = new HashMap<>();

    private static volatile ConstraintPlugins instance = null;

    private static final Object LOCK = new Object();

    public static ConstraintPlugins getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ConstraintPlugins();
                }
            }
        }
        return instance;
    }

    private ConstraintPlugins() {

        final PluginManager manager = new PluginManager(ConstraintType.CATEGORY);
        if (LOGGER instanceof org.apache.logging.log4j.core.Logger) {
            List<String> pluginPackages = ((org.apache.logging.log4j.core.Logger) LOGGER)
                    .getContext()
                    .getConfiguration()
                    .getPluginPackages();
            manager.collectPlugins(pluginPackages);
        } else {
            manager.collectPlugins();
        }
        final Map<String, PluginType<?>> plugins = manager.getPlugins();
        for (Map.Entry<String, PluginType<?>> entry : plugins.entrySet()) {
            try {
                final Class<? extends ConstraintType> clazz =
                        entry.getValue().getPluginClass().asSubclass(ConstraintType.class);
                ConstraintType constraintType = ReflectionUtil.instantiate(clazz);
                constraintMap.put(entry.getKey(), constraintType);
            } catch (final Throwable t) {
                throw new ConstraintCreationException("Unable to create constraint for " + entry.getKey(), t);
            }
        }
    }

    public void validateConstraint(
            boolean isRequestContext,
            String constraint,
            String name,
            String value,
            String constraintValue,
            StringBuilder errors) {
        ConstraintType constraintType = constraintMap.get(constraint.toLowerCase(Locale.US));
        if (constraintType == null) {
            if (errors.length() > 0) {
                errors.append("\n");
            }
            errors.append("Unable to locate constraint type ").append(constraint);
            if (isRequestContext) {
                errors.append(" for ThreadContext key ");
            } else {
                errors.append(" for key ");
            }
            errors.append(name);
            return;
        }
        constraintType.validate(isRequestContext, name, value, constraintValue, errors);
    }

    public ConstraintType findByName(String name) {
        return constraintMap.get(name.toLowerCase(Locale.US));
    }

    public Map<String, ConstraintType> getConstraintMap() {
        return Collections.unmodifiableMap(constraintMap);
    }
}
