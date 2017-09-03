package org.apache.logging.log4j.catalog.service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.dao.CatalogDao;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;
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
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CatalogInitializer {
    private static final Logger logger = LogManager.getLogger(CatalogInitializer.class);

    @Autowired
    AttributeService attributeService;

    @Autowired
    EventService eventService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    CatalogDao catalogDao;

    @Autowired
    AttributeConverter attributeConverter;

    @Autowired
    EventConverter eventConverter;

    @Autowired
    CategoryConverter categoryConverter;

    @Autowired
    ProductConverter productConverter;

    @PostConstruct
    private void initialize() {
        logger.debug("Performing initialization");
        CatalogData catalogData = catalogDao.read();

        logger.debug("Loading attributes");
        List<AttributeModel> attributeModels = new ArrayList<>();
        for (Attribute attribute : catalogData.getAttributes()) {
            AttributeModel model = attributeConverter.convert(attribute);
            attributeService.saveAttribute(model);
            attributeModels.add(model);
        }
        eventConverter.setAttributes(attributeModels);
        Map<String, EventModel> eventMap = new HashMap<>();
        logger.debug("Loading events");
        for (Event event : catalogData.getEvents()) {
            logger.debug("Processing Event: {}", event);
            EventModel model = eventConverter.convert(event);
            logger.debug("Created EventModel: {} ", model);
            eventMap.put(event.getName(), model);
            eventService.saveEvent(model);
        }
        logger.debug("Loading categories");
        for (Category category : catalogData.getCategories()) {
            CategoryModel model = categoryConverter.convert(category);
            categoryService.saveCategory(model);
        }
        logger.debug("loading products");
        for (Product product : catalogData.getProducts()) {
            ProductModel model = productConverter.convert(product);
            productService.saveProduct(model);
        }
    }
}
