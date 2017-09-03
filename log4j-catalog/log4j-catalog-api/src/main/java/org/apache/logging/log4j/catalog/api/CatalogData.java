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

/**
 * Container for the data in the catalog.
 */
public class CatalogData implements Serializable {

    private static final long serialVersionUID = -6772374346223539136L;
    private List<Product> products;
    private List<Category> categories;
    private List<Event> events;
    private List<Attribute> attributes;

    /**
     * Returns the Products associated with the Catalog.
     * @return the List of Products.
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Sets the Products represented in the Catalog.
     *
     * @param products The List of Products.
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * Returns the List of Categories.
     * @return the List of CategoryDto objects or null.
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Sets List of CategoryDto objects.
     * @param categories the List of Categories or null.
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    /**
     * Returns the List of EventDto objects.
     * @return the List of Events.
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Sets the List of EventDto objects.
     * @param events the List of Events or null.
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Returns the List of AttributeDto objects.
     *
     * @return the List of AttributeDto objects or null.
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets the List of AttributeDto objects.
     *
     * @param attributes the List of Attributes.
     */
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * The supported EventLogger types.
     * @return the List of EventLogger type names.
     */
    /* public List<String> getEventLoggerTypes() {
        return eventLoggerTypes;
    } */

    /**
     * Set the List of EventLogger types.
     * @param eventTypes the EventLogger types.
     */
    /* public void setEventLoggerTypes(List<String> eventTypes) {
        this.eventLoggerTypes = eventTypes;
    } */
}
