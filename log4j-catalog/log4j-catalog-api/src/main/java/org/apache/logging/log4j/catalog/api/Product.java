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

import static org.apache.logging.log4j.catalog.api.constant.Constants.DEFAULT_CATALOG;

import java.io.Serializable;
import java.util.List;

/**
 * Definition of a Product.
 */
public class Product implements Serializable {

    private static final long serialVersionUID = -736368842796386523L;
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String catalogId;
    private List<String> events;

    /**
     * Set default values.
     */
    public Product() {
        catalogId = DEFAULT_CATALOG;
    }

    /**
     * Return the id of the Product.
     * @return the Product's id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Product.
     * @param id the Product's id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the product.
     * @return the name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the product.
     * @param name the name of the product.
     * @return this Product.
     */
    public Product setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the name used when displaying the product.
     * @return the display name of the product.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the name to be displayed for this product.
     * @param name the display name for the product.
     * @return this Product.
     */
    public Product setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    /**
     * Return the product description.
     * @return the description of the product.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the product.
     * @param description the description of the product.
     * @return this Product.
     */
    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get the Catalog Id this Product is associated with.
     * @return the catalog id or null.
     */
    public String getCatalogId() {
        return catalogId;
    }

    /**
     * Set the catalog id this Product is associated with.
     * @param catalogId The catalog id or null.
     */
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    /**
     * Returns the List of Event names associated with this product.
     * @return the List of Events.
     */
    public List<String> getEvents() {
        return events;
    }

    /**
     * Sets the List of Event names for this product.
     * @param events the List of Events.
     * @return this Product.
     */
    public Product setEvents(List<String> events) {
        this.events = events;
        return this;
    }
}
