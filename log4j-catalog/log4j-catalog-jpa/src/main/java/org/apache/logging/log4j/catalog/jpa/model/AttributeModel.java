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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import org.apache.logging.log4j.catalog.api.DataType;
import org.apache.logging.log4j.catalog.jpa.converter.BooleanToStringConverter;
import org.apache.logging.log4j.catalog.jpa.converter.DataTypeConverter;

@Entity
@Table(name = "EVENT_ATTRIBUTE",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "NAME" })})
public class AttributeModel implements Serializable {
    private static final long serialVersionUID = -756109102178482698L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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
    @Column(name = "DATATYPE")
    @Convert(converter=DataTypeConverter.class)
    private DataType dataType;
    @Column(name = "INDEXED")
    @Convert(converter=BooleanToStringConverter.class)
    private boolean indexed;
    @Column(name = "SORTABLE")
    @Convert(converter=BooleanToStringConverter.class)
    private boolean sortable;
    @Column(name = "REQUIRED")
    @Convert(converter=BooleanToStringConverter.class)
    private boolean required;
    @Column(name = "REQUEST_CONTEXT")
    @Convert(converter=BooleanToStringConverter.class)
    private boolean requestContext;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "attribute_examples", joinColumns = @JoinColumn(name = "attribute_id"))
    @Column(name = "example")
    private Set<String> examples = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "attribute_aliases", joinColumns = @JoinColumn(name = "attribute_id"))
    @Column(name = "alias")
    private Set<String> aliases = new HashSet<>();
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "attribute", cascade = CascadeType.ALL)
    private Set<ConstraintModel> constraints;

    public AttributeModel() {
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
     * Returns the name of the AttributeDto.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the AttributeDto.
     * @param name the name of the attribute.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name used when displaying the attribute.
     * @return the display name of the attribute.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the name to be displayed for this attribute.
     * @param name the display name for the attribute.
     */
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    /**
     * Returns the description of the attribute.
     * @return the description of the attribute.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the attribute.
     * @param description the description of the attribute.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the data type of this attribute.
     * @return the data type of the attribute.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Set the data type of the attribute.
     * @param dataType the data type of the attribute.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Identifies whether this attribute is an index.
     * @return true if this attribute is an index, false otherwise.
     */
    public boolean isIndexed() {
        return indexed;
    }

    /**
     * Set whether this attribute is an index.
     * @param indexed true if this attribute is an index, false otherwise.
     */
    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    /**
     * Returns whether a sort may be performed on this attribute.
     * @return true if a sort can be performed on this attribute, false otherwise.
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * Set whether a sort may be performed on this attribute.
     * @param sortable true if a sort may be performed on this attribute, false otherwise.
     */
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    /**
     * Returns whether this attribute is required.
     * @return true if this attribute is required, false otherwise.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Set whether this attribute is required.
     * @param required true if this attribute is required, false otherwise.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Returns whether this attribute is part of the RequestContext.
     * @return true if this attribute is part of the RequestContext, false otherwise.
     */
    public boolean isRequestContext() {
        return requestContext;
    }

    /**
     * Set whether this attribute is part of the RequestContext.
     * @param isRequestContext true if this attribute is part of the RequestContext, false otherwise.
     */
    public void setRequestContext(boolean isRequestContext) {
        this.requestContext = isRequestContext;
    }

    /**
     * Returns the List of example Strings.
     * @return the List of example Strings.
     */
    public Set<String> getExamples() {
        return examples;
    }

    /**
     * Sets the List of example Strings.
     * @param examples the List of example Strings.
     */
    public void setExamples(Set<String> examples) {
        this.examples = examples;
    }

    /**
     * Returns the List of alias Strings.
     * @return the List of alias Strings.
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Get the Catalog Id this attribute is associated with.
     * @return the catalog id or null.
     */
    public String getCatalogId() {
        return catalogId;
    }

    /**
     * Set the catalog id this attribute is associated with.
     * @param catalogId The catalog id or null.
     */
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    /**
     * Sets List of alias Strings.
     * @param aliases The List of alias Strings.
     */
    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    public Set<ConstraintModel> getConstraints() {
        return constraints;
    }

    public void setConstraints(Set<ConstraintModel> constraints) {
        if (constraints == null) {
            if (this.constraints == null) {
                this.constraints = new HashSet<>();
            }
        } else {
            for (ConstraintModel constraint : constraints) {
                if (constraint.getAttribute() != this) {
                    constraint.setAttribute(this);
                }
            }
            this.constraints = constraints;
        }
    }

    /*
    public Set<EventAttributeModel> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<EventAttributeModel> attributes) {
        this.attributes = attributes;
    } */

    public int hashCode() {
        return new HashCodeBuilder().append(name).append(catalogId).toHashCode();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof AttributeModel)) return false;

        AttributeModel other = (AttributeModel)o;
        return new EqualsBuilder().append(name, other.name).append(catalogId, other.catalogId).isEquals();
    }
}
