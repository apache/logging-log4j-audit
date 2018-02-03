/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.catalog.jpa.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.EventAttribute;
import org.apache.logging.log4j.catalog.api.constant.Constants;
import org.apache.logging.log4j.catalog.api.exception.CatalogModificationException;
import org.apache.logging.log4j.catalog.jpa.model.AttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.EventAttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.service.AttributeService;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class EventConverter extends AbstractConverter<Event, EventModel> {
    private static Logger LOGGER = LogManager.getLogger(EventConverter.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private AttributeService attributeService;

    public  EventModel convert(Event event) {
        LOGGER.traceEntry(event.getName());
        EventModel model;
        if (event.getId() != null) {
            model = eventService.getEvent(event.getId()).orElseGet(EventModel::new);
        } else {
            model = new EventModel();
        }
        model.setCatalogId(event.getCatalogId());
        model.setName(event.getName());
        model.setAliases(event.getAliases());
        model.setDescription(event.getDescription());
        model.setDisplayName(event.getDisplayName());
        if (model.getAttributes() == null) {
            model.setAttributes(new HashSet<>());
        }
        Set<EventAttributeModel> eventAttributeModels = model.getAttributes() != null ? model.getAttributes() :
                new HashSet<>();
        List<EventAttribute> eventAttributes = event.getAttributes() != null ? event.getAttributes() : new ArrayList<>();
        if (event.getAttributes() != null) {
            for (EventAttribute eventAttribute : eventAttributes) {
                EventAttributeModel eventAttributeModel = model.getAttribute(eventAttribute.getName());
                if (eventAttributeModel != null) {
                    eventAttributeModel.setRequired(eventAttribute != null ? eventAttribute.isRequired() : null);
                } else {
                    Optional<AttributeModel> optional = getAttribute(event.getCatalogId(), eventAttribute.getName());
                    if (optional.isPresent()) {
                        eventAttributeModel = new EventAttributeModel();
                        if (eventAttribute != null) {
                            eventAttributeModel.setRequired(eventAttribute.isRequired());
                        }
                        eventAttributeModel.setEvent(model);
                        eventAttributeModel.setAttribute(optional.get());
                        eventAttributeModels.add(eventAttributeModel);
                    } else {
                        throw new CatalogModificationException("No catalog entry for " + eventAttribute.getName());
                    }
                }
            }
        }
        eventAttributeModels.removeIf(a -> eventAttributes.stream().noneMatch(b -> b.getName().equals(a.getAttribute().getName())));
        model.setAttributes(eventAttributeModels);
        return LOGGER.traceExit(model);
    }

    private Optional<AttributeModel> getAttribute(String catalogId, String name) {
        Optional<AttributeModel> optional = attributeService.getAttribute(catalogId, name);
        if (!optional.isPresent()) {
            optional = attributeService.getAttribute(Constants.DEFAULT_CATALOG, name);
        }
        return optional;
    }
}
