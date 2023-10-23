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

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.logging.log4j.catalog.jpa.converter.BooleanToStringConverter;

/**
 *
 */
@Entity
@Table(name = "event_attributes")
public class EventAttributeModel implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private EventModel event;

    @ManyToOne
    @JoinColumn(name = "attribute_id", referencedColumnName = "id")
    private AttributeModel attribute;

    @Column(name = "is_required")
    @Convert(converter=BooleanToStringConverter.class)
    private Boolean isRequired;

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

    public EventModel getEvent() {
        return event;
    }

    public void setEvent(EventModel event) {
        this.event = event;
    }

    public AttributeModel getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeModel attribute) {
        this.attribute = attribute;
    }

    public Boolean isRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventAttributeModel that = (EventAttributeModel) o;

        if (!event.equals(that.event)) {
            return false;
        }
        return attribute.equals(that.attribute);
    }

    @Override
    public int hashCode() {
        int result = event == null ? 0 : event.hashCode();
        result = 31 * result + attribute.hashCode();
        return result;
    }
}
