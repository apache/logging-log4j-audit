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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(
        name = "CATALOG_EVENT",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public class EventModel implements Serializable {
    private static final long serialVersionUID = 1512172827909901054L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CATALOG_ID")
    private String catalogId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_aliases", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "alias")
    private Set<String> aliases;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventAttributeModel> attributes = new HashSet<>();

    public EventModel() {
        catalogId = "DEFAULT";
    }

    /**
     * Return the identifier for this event.
     * @return the identifier for this event.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the identifier for this event.
     * @param id the identifier for this event.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name for this event.
     * @return the name for this event.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name for this event.
     * @param name the name for this event.
     */
    public void setName(String name) {
        this.name = name;
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
     */
    public void setDisplayName(String name) {
        this.displayName = name;
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
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the List of alias Strings.
     * @return the List of alias Strings.
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Sets the List of alias Strings.
     * @param aliases the List of alias Strings.
     */
    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
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
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    /**
     * Returns the List of AttributeDto objects.
     * @return the List of Attributes.
     */
    public Set<EventAttributeModel> getAttributes() {
        return attributes;
    }

    public List<String> getAttributeNames() {
        List<String> names = new ArrayList<>(attributes.size());
        for (EventAttributeModel model : attributes) {
            names.add(model.getAttribute().getName());
        }
        return names;
    }

    public EventAttributeModel getAttribute(String name) {
        for (EventAttributeModel model : attributes) {
            if (name.equals(model.getAttribute().getName())) {
                return model;
            }
        }
        return null;
    }

    public void addEventAttribute(EventAttributeModel attribute) {
        this.attributes.add(attribute);
    }

    /**
     * Sets the List of Atribute objects.
     * @param attributes The List of Attributes.
     */
    public void setAttributes(Set<EventAttributeModel> attributes) {
        this.attributes = attributes;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(name).toHashCode();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof EventModel)) return false;

        EventModel other = (EventModel) o;
        return new EqualsBuilder().append(name, other.name).isEquals();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\" : \"").append(id).append("\"");
        sb.append(", \"name\" : \"")
                .append(name)
                .append("\", \"displayName\" : \"")
                .append(displayName)
                .append("\"");
        sb.append(", \"description\" : ").append(description).append("\", \"attributes\" : [");
        boolean first = true;
        for (EventAttributeModel attribute : attributes) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append("{\"name\" : \"")
                    .append(attribute.getAttribute().getName())
                    .append("\", \"required\" : ")
                    .append(attribute.isRequired())
                    .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }
}
