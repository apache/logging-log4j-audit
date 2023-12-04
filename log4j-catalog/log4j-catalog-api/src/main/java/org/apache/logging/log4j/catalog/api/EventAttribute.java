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
package org.apache.logging.log4j.catalog.api;

import java.io.Serializable;

/**
 * Attribute used in an event
 */
public class EventAttribute implements Serializable {

    private static final long serialVersionUID = 6234766849875135836L;
    private String name;

    private Boolean isRequired;

    public EventAttribute() {}

    public EventAttribute(String name, boolean isRequired) {
        this.name = name;
        this.isRequired = isRequired;
    }

    /**
     * The name of the attribute.
     * @return the name of the Attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Attribute.
     * @param attributeName the Attribute's name.
     */
    public void setName(String attributeName) {
        this.name = attributeName;
    }

    /**
     * Indicates whether the attribute is required.
     * @return true if the Attribute is required, false otherwise.
     */
    public Boolean isRequired() {
        return isRequired;
    }

    /**
     * Sets whether the attribute is required.
     * @param required true if the attribute is required, false otherwise.
     */
    public void setRequired(Boolean required) {
        isRequired = required;
    }
}
