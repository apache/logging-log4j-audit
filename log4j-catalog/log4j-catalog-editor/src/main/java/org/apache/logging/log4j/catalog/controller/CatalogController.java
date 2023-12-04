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
package org.apache.logging.log4j.catalog.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;
import org.apache.logging.log4j.catalog.api.dao.CatalogDao;
import org.apache.logging.log4j.catalog.jpa.converter.AttributeModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.CategoryModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.EventModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.ProductModelConverter;
import org.apache.logging.log4j.catalog.jpa.model.AttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.CategoryModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.model.ProductModel;
import org.apache.logging.log4j.catalog.jpa.service.AttributeService;
import org.apache.logging.log4j.catalog.jpa.service.CategoryService;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.apache.logging.log4j.catalog.jpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class CatalogController.
 */
@RestController
public class CatalogController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttributeModelConverter attributeModelConverter;

    @Autowired
    private EventModelConverter eventModelConverter;

    @Autowired
    private ProductModelConverter productModelConverter;

    @Autowired
    private CategoryModelConverter categoryModelConverter;

    @Autowired
    private CatalogDao catalogDao;

    @PostMapping(value = "catalog")
    public ResponseEntity<?> saveCatalog() {
        CatalogData catalogData = new CatalogData();
        List<Attribute> attributes = new ArrayList<>();
        for (AttributeModel attributeModel : attributeService.getAttributes()) {
            attributes.add(attributeModelConverter.convert(attributeModel));
        }
        catalogData.setAttributes(attributes);
        List<Event> events = new ArrayList<>();
        for (EventModel eventModel : eventService.getEvents()) {
            events.add(eventModelConverter.convert(eventModel));
        }
        catalogData.setEvents(events);
        List<Category> categories = new ArrayList<>();
        for (CategoryModel categoryModel : categoryService.getCategories()) {
            categories.add(categoryModelConverter.convert(categoryModel));
        }
        catalogData.setCategories(categories);
        List<Product> products = new ArrayList<>();
        for (ProductModel productModel : productService.getProducts()) {
            products.add(productModelConverter.convert(productModel));
        }
        catalogData.setProducts(products);
        catalogDao.write(catalogData);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    /*
    @RequestMapping(value = "/catalog", method = RequestMethod.GET)
    public ResponseEntity<Object> handleGetCatalog(
            @RequestParam(required = false) boolean attributeDetails,
            HttpServletRequest servletRequest) {
        CatalogData catalogData = null;
        try {
            //catalogData = globalLoggingCatalog.getCatalog();
            if (attributeDetails) {
                getAttributeDetailsForEvents(catalogData);
            }
            return new ResponseEntity<Object>(catalogData, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error While Retrieving Data", e);

            Status status = new Status();
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setErrorCode("00000");
            errorInfo.setErrorMessage(e.getMessage());
            status.getErrorInfo().add(errorInfo);
            status.setStatusMessage(e.getMessage());
            return new ResponseEntity<Object>(status,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }


    } */

}
