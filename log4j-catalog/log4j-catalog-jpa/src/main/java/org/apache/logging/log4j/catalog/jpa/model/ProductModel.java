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
package org.apache.logging.log4j.catalog.jpa.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Definition of a ProductDto.
 */
@Entity
@Table(
        name = "CATALOG_PRODUCT",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public class ProductModel implements Serializable {
    private static final long serialVersionUID = -736368842796386523L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CATALOG_ID")
    private String catalogId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "PRODUCT_EVENTS",
            joinColumns = {@JoinColumn(name = "PRODUCT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "EVENT_ID")})
    private List<EventModel> events;

    public ProductModel() {
        catalogId = "DEFAULT";
    }

    public Long getId() {
        return id;
    }

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
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The value used when displaying the category name.
     * @return the display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value to be used when displaying the name.
     * @param dislpayName The display name.
     */
    public void setDisplayName(String dislpayName) {
        this.displayName = dislpayName;
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
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Returns the List of EventDto objects associated with this product.
     * @return the List of Events.
     */
    public List<EventModel> getEvents() {
        return events;
    }

    /**
     * Sets the List of EventDto objects.
     * @param events the List of Events.
     */
    public void setEvents(List<EventModel> events) {
        this.events = events;
    }
}
