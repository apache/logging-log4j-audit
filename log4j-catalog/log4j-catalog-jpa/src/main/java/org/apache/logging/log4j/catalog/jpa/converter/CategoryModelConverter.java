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

import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.jpa.model.CategoryModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryModelConverter extends AbstractConverter<CategoryModel, Category> {

    @Autowired
    private EventService eventService;

    public Category convert(CategoryModel categoryModel) {
        Category category = new Category();
        category.setId(categoryModel.getId());
        category.setCatalogId(categoryModel.getCatalogId());
        category.setName(categoryModel.getName());
        category.setDisplayName(categoryModel.getDisplayName());
        category.setDescription(categoryModel.getDescription());
        List<String> events = new ArrayList<>(categoryModel.getEvents().size());
        for (EventModel event : categoryModel.getEvents()) {
            events.add(event.getName());
        }
        category.setEvents(events);
        return category;
    }
}
