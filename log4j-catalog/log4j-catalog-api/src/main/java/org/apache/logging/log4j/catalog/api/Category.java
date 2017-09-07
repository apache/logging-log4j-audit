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

import com.fasterxml.jackson.annotation.JsonIgnore;

import static org.apache.logging.log4j.catalog.api.constant.Constants.DEFAULT_CATALOG;

/**
 * A Catalog Category.
 */
public class Category implements Serializable {

    private static final long serialVersionUID = 5776108323599073407L;
    private Long id;
    private String name;
    private String displayName;
    private String description;
    @JsonIgnore
    private String catalogId;
    private List<String> events;

    /**
     * Set default values;
     */
    public Category() {
        catalogId = DEFAULT_CATALOG;
    }

    /**
     * Return the id of the Category.
     * @return the Category's id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Category.
     * @param id the id of the Category.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     * @return this Category.
     */
    public Category setName(String value) {
        this.name = value;
        return this;
    }

    /**
     * Returns the name used when displaying the category.
     * @return the display name of the category.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the name to be displayed for this category.
     * @param name the display name for the category.
     * @return this Category.
     */
    public Category setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value The description of the category.
     * @return this Category.
     */
    public Category setDescription(String value) {
        this.description = value;
        return this;
    }

    /**
     * Get the Catalog Id this Category is associated with.
     * @return the catalog id or null.
     */
    public String getCatalogId() {
        return catalogId;
    }

    /**
     * Set the catalog id this Category is associated with.
     * @param catalogId The catalog id or null.
     */
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    /**
     * Return the List of Event names.
     * @return the List of Event names or null.
     */
    public List<String> getEvents() {
        return events;
    }

    /**
     * Sets the List of Event names.
     * @param events the List of Events.
     */
    public Category setEvents(List<String> events) {
        this.events = events;
        return this;
    }
}
