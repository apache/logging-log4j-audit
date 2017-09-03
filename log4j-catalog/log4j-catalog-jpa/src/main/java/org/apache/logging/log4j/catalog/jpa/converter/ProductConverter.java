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

import org.apache.logging.log4j.catalog.api.Product;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.model.ProductModel;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter extends AbstractConverter<Product, ProductModel> {

    @Autowired
    private EventService eventService;

    public ProductModel convert(Product product) {
        Map<String, EventModel> eventMap = eventService.getEventMap();
        ProductModel model = new ProductModel();
        model.setId(product.getId());
        model.setName(product.getName());
        model.setDescription(product.getDescription());
        model.setDisplayName(product.getDisplayName());
        model.setCatalogId(product.getCatalogId());
        List<EventModel> events = new ArrayList<>(product.getEvents().size());
        for (String name : product.getEvents()) {
            EventModel event = eventMap.get(name);
            if (event != null) {
                events.add(event);
            } else {
                throw new IllegalArgumentException("Unknown event " + name + " for product " + product.getName());
            }
        }
        model.setEvents(events);
        return model;
    }
}
