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
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.jpa.model.CategoryModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter extends AbstractConverter<Category, CategoryModel> {

    @Autowired
    private EventService eventService;

    public CategoryModel convert(Category category) {
        Map<String, EventModel> eventMap = eventService.getEventMap();
        CategoryModel model = new CategoryModel();
        model.setId(category.getId());
        model.setCatalogId(category.getCatalogId());
        model.setName(category.getName());
        model.setDescription(category.getDescription());
        model.setDisplayName(category.getDisplayName());
        List<EventModel> events = new ArrayList<>(category.getEvents().size());
        for (String name : category.getEvents()) {
            EventModel event = eventMap.get(name);
            if (event != null) {
                events.add(event);
            } else {
                throw new IllegalArgumentException("Unknown event " + name + " for category " + category.getName());
            }
        }
        model.setEvents(events);
        return model;
    }
}
