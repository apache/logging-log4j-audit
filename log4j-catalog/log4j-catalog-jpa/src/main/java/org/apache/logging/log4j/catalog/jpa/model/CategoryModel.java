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
package org.apache.logging.log4j.catalog.jpa.model;

import javax.persistence.CascadeType;
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
import java.io.Serializable;
import java.util.List;

/**
 * A Catalog CategoryDto.
 */
@Entity
@Table(name = "CATALOG_CATEGORY",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "NAME" })})
public class CategoryModel implements Serializable {
    private static final long serialVersionUID = 5776108323599073407L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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
    @JoinTable(name = "category_events", joinColumns = { @JoinColumn(name = "category_id")},
            inverseJoinColumns = { @JoinColumn(name = "event_id")})
    private List<EventModel> events;

    public CategoryModel() {
        catalogId = "DEFAULT";
    }

    /**
     * Returns the id of the AttributeDto.
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the AttributeDto.
     * @param id
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
     */
    public void setName(String value) {
        this.name = value;
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
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
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
     * Return the List of EventDto objects.
     * @return the List of Events or null.
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
