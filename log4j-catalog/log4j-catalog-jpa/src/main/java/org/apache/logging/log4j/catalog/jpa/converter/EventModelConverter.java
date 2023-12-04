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
package org.apache.logging.log4j.catalog.jpa.converter;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.EventAttribute;
import org.apache.logging.log4j.catalog.jpa.model.EventAttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class EventModelConverter extends AbstractConverter<EventModel, Event> {

    public Event convert(EventModel model) {
        Event event = new Event();
        event.setName(model.getName());
        event.setDisplayName(model.getDisplayName());
        event.setDescription(model.getDescription());
        event.setAliases(model.getAliases());
        event.setId(model.getId());
        event.setCatalogId(model.getCatalogId());
        List<EventAttribute> attributes = new ArrayList<>();
        if (model.getAttributes() != null) {
            for (EventAttributeModel eventAttributeModel : model.getAttributes()) {
                EventAttribute eventAttribute = new EventAttribute();
                eventAttribute.setName(eventAttributeModel.getAttribute().getName());
                eventAttribute.setRequired(eventAttributeModel.isRequired());
                attributes.add(eventAttribute);
            }
        }
        event.setAttributes(attributes);
        return event;
    }
}
