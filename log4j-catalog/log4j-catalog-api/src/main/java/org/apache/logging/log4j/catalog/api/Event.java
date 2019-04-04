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
package org.apache.logging.log4j.catalog.api;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import static org.apache.logging.log4j.catalog.api.constant.Constants.DEFAULT_CATALOG;

/**
 * Basic attributes common to all events.
 */
@JsonFilter("catalogEvent")
public class Event implements Serializable {

    private static final long serialVersionUID = 1512172827909901054L;
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private Set<String> aliases;
    private String catalogId;
    private List<EventAttribute> attributes;

    /**
     * Set default values.
     */
    public Event() {
        catalogId = DEFAULT_CATALOG;
    }
    
    /**
     * Return the id or the event.
     * @return the Event's id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the event's id.
     * @param id the Event's id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Return the name of the event.
     * @return the name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event.
     * @param name The name of the event.
     * @return this Event.
     */
    public Event setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the name to display for this event.
     * @return the display name for this event.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the display name for this event.
     * @param name the name to display for this event.
     * @return this Event.
     */
    public Event setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    /**
     * Return the description of the event.
     * @return the event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the event description.
     * @param description The description of the event.
     * @return this Event.
     */
    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get the Catalog Id this Event is associated with.
     * @return the catalog id or null.
     */
    public String getCatalogId() {
        return catalogId;
    }

    /**
     * Set the catalog id this Event is associated with.
     * @param catalogId The catalog id or null.
     */
    public Event setCatalogId(String catalogId) {
        if (catalogId != null) {
            this.catalogId = catalogId;
        }
        return this;
    }

    /**
     * Returns the Set of alias Strings.
     * @return the Set of alias Strings.
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Sets the Set of alias Strings.
     * @param aliases the Set of alias Strings.
     * @return this Event.
     */
    public Event setAliases(Set<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * Returns the List of Attribute names.
     * @return the List of Attribute names.
     */
    public List<EventAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets the List of Atribute names.
     * @param attributes The List of Attribute names.
     * @return this Event.
     */
    public Event setAttributes(List<EventAttribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"name\" : \"").append(name).append("\", \"displayName\" : \"").append(displayName).append("\"");
        sb.append(", \"description\" : \"").append(description).append("\", \"attributes\" : [");
        if (attributes != null) {
            boolean first = true;
            for (EventAttribute attribute : attributes) {
                if (!first) {
                    sb.append(", ");
                } else {
                    first = false;
                }
                sb.append("{\"name\" : \"").append(attribute.getName()).append("\", \"required\" : ").append(attribute.isRequired()).append("}");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
