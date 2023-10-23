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
package org.apache.logging.log4j.catalog.jpa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;
import org.apache.logging.log4j.catalog.api.plugins.ConstraintPlugins;
import org.apache.logging.log4j.catalog.jpa.converter.AttributeModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.CategoryModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.EventModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.ProductModelConverter;
import org.apache.logging.log4j.catalog.jpa.dao.AttributeRepository;
import org.apache.logging.log4j.catalog.jpa.dao.CatalogRepository;
import org.apache.logging.log4j.catalog.jpa.dao.CategoryRepository;
import org.apache.logging.log4j.catalog.jpa.dao.EventRepository;
import org.apache.logging.log4j.catalog.jpa.dao.ProductRepository;
import org.apache.logging.log4j.catalog.jpa.model.AttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.CatalogModel;
import org.apache.logging.log4j.catalog.jpa.model.CategoryModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Repository
@Transactional(readOnly = false)
public class CatalogServiceImpl implements CatalogService {

    private static final ConstraintPlugins constraintPlugins = ConstraintPlugins.getInstance();

    @Autowired
    private AttributeRepository attributeRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AttributeModelConverter attributeModelConverter;
    @Autowired
    private EventModelConverter eventModelConverter;
    @Autowired
    private CategoryModelConverter categoryModelConverter;
    @Autowired
    private ProductModelConverter productModelConverter;
    @Autowired
    private CatalogRepository catalogRepository;


    public CatalogData getCatalogData() {
        CatalogData data = new CatalogData();

        List<AttributeModel> modelAttributes = attributeRepository.findAll();
        List<org.apache.logging.log4j.catalog.api.Attribute> attributes = new ArrayList<>(modelAttributes.size());
        for (AttributeModel modelAttribute : modelAttributes) {
            Attribute attribute = attributeModelConverter.convert(modelAttribute);
            attributes.add(attribute);
        }
        data.setAttributes(attributes);

        List<EventModel> modelEvents = eventRepository.findAll();
        List<org.apache.logging.log4j.catalog.api.Event> events = new ArrayList<>(modelEvents.size());
        for (EventModel modelEvent : modelEvents) {
            Event event = eventModelConverter.convert(modelEvent);
            events.add(event);
        }
        data.setEvents(events);

        List<CategoryModel> modelCategories = categoryRepository.findAll();
        List<org.apache.logging.log4j.catalog.api.Category> categories = new ArrayList<>(modelCategories.size());
        for (CategoryModel modelCategory : modelCategories) {
            Category category = categoryModelConverter.convert(modelCategory);
            categories.add(category);
        }
        data.setCategories(categories);

        List<ProductModel> modelProducts = productRepository.findAll();
        List<Product> products = new ArrayList<>(modelProducts.size());
        for (ProductModel modelProduct : modelProducts) {
            Product product = productModelConverter.convert(modelProduct);
            products.add(product);
        }
        data.setProducts(products);

        return data;
    }

    public List<CategoryModel> getCategories() {
        return categoryRepository.findAll();
    }

    public Optional<CategoryModel> getCategory(String name) {
        return categoryRepository.findByName(name);
    }

    public Optional<CategoryModel> getCategory(long id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public CatalogModel getCatalogModel() {
        List<CatalogModel> catalogModels = catalogRepository.findAll();
        if (catalogModels != null && catalogModels.size() > 0) {
            return catalogModels.get(0);
        }
        return null;
    }

    @Override
    public void saveCatalog(CatalogModel catalogModel) {
        catalogRepository.save(catalogModel);
    }
}
