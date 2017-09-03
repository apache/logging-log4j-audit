/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.audit.dto;

import java.util.Map;

/**
 * Container for Audit Event data.
 */
public class AuditDto {
    /**
     * The name of the event.
     */
    private String eventName;

    /**
     * The RequestContext Map.
     */
    private Map<String, String> requestContextMap;

    /**
     * The event specific attributes.
     */
    private Map<String, String> properties;

    /**
     * Get the name of the event.
     * @return the name of the event.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Set the name of the event.
     * @param eventName The name of the event.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Returns the RequestContext data map.
     * @return A Map containing all the RequestContext keys and values.
     */
    public Map<String, String> getRequestContextMap() {
        return requestContextMap;
    }

    /**
     * Set the RequstContext Map.
     * @param requestContextMap the RequestContext Map.
     */
    public void setRequestContextMap(Map<String, String> requestContextMap) {
        this.requestContextMap = requestContextMap;
    }

    /**
     * Gets the Map of properties for this event.
     * @return the Map of event properties.
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Sets the RequestContext properties.
     * @param properties The RequestContext properties.
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
