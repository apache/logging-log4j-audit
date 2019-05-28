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
package org.apache.logging.log4j.audit.service.catalog;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.audit.catalog.CatalogManagerImpl;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;
import org.apache.logging.log4j.catalog.jpa.model.CatalogModel;
import org.apache.logging.log4j.catalog.jpa.service.CatalogService;
import org.apache.logging.log4j.catalog.jpa.converter.AttributeConverter;
import org.apache.logging.log4j.catalog.jpa.converter.CategoryConverter;
import org.apache.logging.log4j.catalog.jpa.converter.EventConverter;
import org.apache.logging.log4j.catalog.jpa.converter.ProductConverter;
import org.apache.logging.log4j.catalog.jpa.model.AttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.CategoryModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.model.ProductModel;
import org.apache.logging.log4j.catalog.jpa.service.AttributeService;
import org.apache.logging.log4j.catalog.jpa.service.CategoryService;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.apache.logging.log4j.catalog.jpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditCatalogManager extends CatalogManagerImpl implements AuditManager {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private CatalogService catalogService;

    @Autowired
    AttributeService attributeService;

    @Autowired
    EventService eventService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    AttributeConverter attributeConverter;

    @Autowired
    EventConverter eventConverter;

    @Autowired
    CategoryConverter categoryConverter;

    @Autowired
    ProductConverter productConverter;

    private final CatalogReader catalogReader;


    public AuditCatalogManager(CatalogReader catalogReader) {
        super(catalogReader);
        this.catalogReader = catalogReader;
    }

    @PostConstruct
    public void initialize() {
        CatalogModel catalogModel = catalogService.getCatalogModel();
        if (catalogModel == null) {
            catalogModel = new CatalogModel();
            initialize(catalogModel);
        } else if (catalogModel.getLastUpdate().toLocalDateTime().isBefore(catalogReader.getLastUpdated())) {
            initialize(catalogModel);
        }
    }

    @Override
    public EventModel saveEvent(Event event) {
        EventModel model = eventConverter.convert(event);
        model = eventService.saveEvent(model);
        Map<String, Map<String, CatalogInfo>> infoMap = getInfoMap();
        addEntry(infoMap, event);
        return model;
    }

    @Override
    public void saveAttribute(Attribute attribute) {
        Map<String, Attribute> attrMap = attributeMap.get(attribute.getCatalogId());
        if (attrMap == null) {
            attrMap = new ConcurrentHashMap<>();
            attributeMap.put(attribute.getCatalogId(), attrMap);
        }
        attrMap.put(attribute.getName(), attribute);
    }

    private void initialize(CatalogModel catalogModel) {
        logger.debug("Updating static catalog");

        logger.debug("Loading attributes");
        List<AttributeModel> attributeModels = new ArrayList<>();
        Map<String, Attribute> attributeMap = new HashMap<>();
        for (Attribute attribute : catalogData.getAttributes()) {
            AttributeModel model = attributeConverter.convert(attribute);
            attributeService.saveAttribute(model);
            attributeModels.add(model);
            attributeMap.put(attribute.getName(), attribute);
        }
        for (AttributeModel attributeModel : attributeModels) {
            if (!attributeMap.containsKey(attributeModel.getName())) {
                attributeService.deleteAttribute(attributeModel.getId());
            }
        }
        Map<String, Event> eventMap = new HashMap<>();
        List<EventModel> eventModels = new ArrayList<>();
        logger.debug("Loading events");
        for (Event event : catalogData.getEvents()) {
            logger.debug("Processing Event: {}", event);
            EventModel model = eventConverter.convert(event);
            eventMap.put(event.getName(), event);
            eventModels.add(model);
            eventService.saveEvent(model);
        }
        for (EventModel eventModel : eventModels) {
            if (!eventMap.containsKey(eventModel.getName())) {
                eventService.deleteEvent(eventModel.getId());
            }
        }
        List<CategoryModel> categoryModels = new ArrayList<>();
        Map<String, Category> categoryMap = new HashMap<>();
        logger.debug("Loading categories");
        for (Category category : catalogData.getCategories()) {
            CategoryModel model = categoryConverter.convert(category);
            categoryModels.add(model);
            categoryMap.put(category.getName(), category);
            categoryService.saveCategory(model);
        }
        for (CategoryModel categoryModel : categoryModels) {
            if (!categoryMap.containsKey(categoryModel.getName())) {
                categoryService.deleteCategory(categoryModel.getId());
            }
        }
        List<ProductModel> productModels = new ArrayList<>();
        Map<String, Product> productMap = new HashMap<>();
        logger.debug("loading products");
        for (Product product : catalogData.getProducts()) {
            ProductModel model = productConverter.convert(product);
            productModels.add(model);
            productMap.put(product.getName(), product);
            productService.saveProduct(model);
        }
        for (ProductModel productModel : productModels) {
            if (!productMap.containsKey(productModel.getName())) {
                productService.deleteProduct(productModel.getId());
            }
        }

        catalogModel.setLastUpdate(Timestamp.from(Instant.now()));
        catalogService.saveCatalog(catalogModel);
    }
}
