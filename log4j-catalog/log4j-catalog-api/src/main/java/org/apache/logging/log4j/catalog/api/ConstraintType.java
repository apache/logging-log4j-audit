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
package org.apache.logging.log4j.catalog.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.logging.log4j.catalog.api.exception.NameNotFoundException;
import org.apache.logging.log4j.catalog.api.plugins.ConstraintTypeDeserializer;
import org.apache.logging.log4j.catalog.api.plugins.ConstraintTypeSerializer;
import org.apache.logging.log4j.core.config.plugins.Plugin;

/**
 *
 */
@JsonDeserialize(using = ConstraintTypeDeserializer.class)
@JsonSerialize(using = ConstraintTypeSerializer.class)
public interface ConstraintType {

    String CATEGORY = "Constraint";

    default String getName() {
        Plugin annotation = this.getClass().getAnnotation(Plugin.class);
        if (annotation == null || annotation.name().length() == 0) {
            throw new NameNotFoundException("No name could be found for plugin class " + this.getClass().getName());
        }

        return annotation.name();
    }

    void validate(boolean isRequestContext, String name, String value, String constraintValue, StringBuilder error);

}
