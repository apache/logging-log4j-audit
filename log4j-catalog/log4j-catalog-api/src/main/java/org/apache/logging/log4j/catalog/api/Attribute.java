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
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Catalog AttributeDto.
 */
public class Attribute implements Serializable {

    private static final long serialVersionUID = -756109102178482698L;
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private DataType dataType;
    private boolean indexed;
    private boolean sortable;
    private boolean required;
    private boolean requestContext;
    private Set<String> examples;
    private Set<String> aliases;
    private Set<Constraint> constraints;
    @JsonIgnore
    private String catalogId;

    /**
     * Set default values.
     */
    public Attribute() {
        catalogId = "DEFAULT"; 
    }

    /**
     * Return the attribute's id.
     * @return the Attribute's id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the Attribute's id.
     * @param id the Attribute's id.
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
     * @return this Attribute.
     */
    public Attribute setName(String name) {
        this.name = name;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setDisplayName(String name) {
        this.displayName = name;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setDescription(String description) {
        this.description = description;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setDataType(DataType dataType) {
        this.dataType = dataType;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setIndexed(boolean indexed) {
        this.indexed = indexed;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setSortable(boolean sortable) {
        this.sortable = sortable;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setRequired(boolean required) {
        this.required = required;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setRequestContext(boolean isRequestContext) {
        this.requestContext = isRequestContext;
        return this;
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
     * @return this Attribute.
     */
    public Attribute setExamples(Set<String> examples) {
        this.examples = examples;
        return this;
    }

    /**
     * Returns the List of alias Strings.
     * @return the List of alias Strings.
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Sets List of alias Strings.
     * @param aliases The List of alias Strings.
     * @return this Attribute.
     */
    public Attribute setAliases(Set<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * Returns the constraints on this attribute.
     * @return The list of constraints.
     */
    public Set<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Sets the Constraints onf the attribute.
     * @param constraints The List of constraints.
     * @return This Attribute.
     */
    public Attribute setConstraints(Set<Constraint> constraints) {
        this.constraints = constraints;
        return this;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("id=\"").append(id).append("\" ");
        sb.append("catalog id=\"").append(catalogId).append("\" ");
        sb.append("name=\"").append(name).append("\" ");
        sb.append("displayName=\"").append(displayName).append("\" ");
        sb.append("description=\"").append(description).append("\" ");
        sb.append("dataType=\"");
        if (dataType == null) {
            sb.append("null");
        } else {
            sb.append(dataType.getTypeName());
        }
        sb.append("\" ");
        sb.append("indexed=\"").append(indexed).append("\" ");
        sb.append("sortable=\"").append(sortable).append("\" ");
        sb.append("required=\"").append(required).append("\" ");
        sb.append("requestContext=\"").append(requestContext).append("\" ");
        if (constraints != null) {
            sb.append("constraints[");
            boolean first = true;
            for (Constraint constraint : constraints) {
                if (!first) {
                    sb.append(" ");
                }
                sb.append("name=\"").append(constraint.getConstraintType().getName()).append("\"");
                sb.append("value=\"").append(constraint.getValue()).append("\"");
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }
}
