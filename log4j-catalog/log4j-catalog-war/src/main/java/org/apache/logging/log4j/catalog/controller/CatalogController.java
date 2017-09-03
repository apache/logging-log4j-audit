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
package org.apache.logging.log4j.catalog.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.dao.CatalogDao;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.logging.log4j.catalog.api.CatalogData;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class CatalogController.
 */
@RestController
public class CatalogController {

	/** The logger. */
	private static Logger logger = LogManager.getLogger(CatalogController.class);

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


	@PostMapping(value = "/catalog/products/list")
	public ResponseEntity<List<Product>> productList() {
		return null;
	}

	@RequestMapping(value = "/catalog/events", method = RequestMethod.GET)
	public ResponseEntity<Object> handleGetEventsList(
			@RequestParam(required = false) String eventList,
			@RequestParam(required = false) boolean attributeDetails,
			HttpServletRequest servletRequest) throws ParseException {

		CatalogData catalogData = null;
/*		try {
			if (!GenericValidator.isBlankOrNull(eventList)) {
				List<String> events = new ArrayList<>(StringUtils.commaDelimitedListToSet(eventList));
				catalogData = new CatalogData();
				catalogData.setEvents(new Events());
				for (String eventID : events) {
					globalLoggingCatalog.getEventById(eventID, catalogData.getEvents().getEvent());
				}
			} else {
				catalogData = globalLoggingCatalog.getEvents();
			}

			if (attributeDetails && catalogData != null
					&& catalogData.getEvents() != null) {
				List<Event> events = catalogData.getEvents().getEvent();
				for (Event event : events) {
					if (event != null && event.getEventAttributes() != null) {
						event.setAttributes(new Attributes());
						List<String> attributes = event.getEventAttributes()
								.getAttribute();
						for (String eventAttribute : attributes) {
							globalLoggingCatalog.getAttributeByName(
									eventAttribute, event.getAttributes()
											.getAttribute());
						}
					}
				}
			}
		} catch (GLCatalogException e) {
			logger.error("Error While Retrieving Data", e);

			Status status = new Status();
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.setErrorCode("00000");
			errorInfo.setErrorMessage(e.getMessage());
			status.getErrorInfo().add(errorInfo);
			status.setStatusMessage(e.getMessage());
			return new ResponseEntity<Object>(status,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} */

		return new ResponseEntity<Object>(catalogData, HttpStatus.OK);

	}

	@RequestMapping(value = "/catalog/events/{eventID}", method = RequestMethod.GET)
	public ResponseEntity<Object> handleGetEvent(
			@PathVariable("eventID") String eventID,
			@RequestParam(required = false) boolean attributeDetails,
			HttpServletRequest servletRequest) {

		CatalogData catalogData = null;
	/*	try {
			catalogData = globalLoggingCatalog.getEventById(eventID);

			if (attributeDetails
					&& catalogData != null
					&& catalogData.getEvents() != null
					&& catalogData.getEvents().getEvent().get(0) != null
					&& catalogData.getEvents().getEvent().get(0)
							.getEventAttributes() != null) {
				List<String> eventAttributes = catalogData.getEvents()
						.getEvent().get(0).getEventAttributes().getAttribute();
				logger.debug("event attributes count: "
						+ eventAttributes.size());
				catalogData.getEvents().getEvent().get(0)
						.setAttributes(new Attributes());
				for (String eventAttribute : eventAttributes) {
					globalLoggingCatalog.getAttributeByName(eventAttribute,
							catalogData.getEvents().getEvent().get(0)
									.getAttributes().getAttribute());
				}
			}

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
		} */

		return new ResponseEntity<Object>(catalogData, HttpStatus.OK);

	}

	@RequestMapping(value = "/catalog/attributes", method = RequestMethod.GET)
	public ResponseEntity<Object> handleGetAttributesList(
			@RequestParam(required = false) String attributeList,
			HttpServletRequest servletRequest) {

		CatalogData catalogData = null;
	/*	try {

			if (!GenericValidator.isBlankOrNull(attributeList)) {
				List<String> attributes = new ArrayList<String>(
						StringUtils.commaDelimitedListToSet(attributeList));
				catalogData = new CatalogData();
				catalogData.setAttributes(new Attributes());
				for (String attribute : attributes) {
					globalLoggingCatalog.getAttributeByName(attribute,
							catalogData.getAttributes().getAttribute());
				}
			} else {

				catalogData = globalLoggingCatalog.getAttributes();
			}
		} catch (GLCatalogException e) {
			logger.error("Error While Retrieving Data", e);

			Status status = new Status();
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.setErrorCode("00000");
			errorInfo.setErrorMessage(e.getMessage());
			status.getErrorInfo().add(errorInfo);
			status.setStatusMessage(e.getMessage());
			return new ResponseEntity<Object>(status,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} */

		return new ResponseEntity<Object>(catalogData, HttpStatus.OK);

	}

	@RequestMapping(value = "/catalog/attributes/{attribute}", method = RequestMethod.GET)
	public ResponseEntity<Object> handleGetAttribute(
			@PathVariable("attribute") String attribute,
			HttpServletRequest servletRequest) throws ParseException {

		CatalogData catalogData = null;
/*		try {
			catalogData = globalLoggingCatalog.getAttributeByName(attribute);
		} catch (GLCatalogException e) {
			logger.error("Error While Retrieving Data", e);

			Status status = new Status();
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.setErrorCode("00000");
			errorInfo.setErrorMessage(e.getMessage());
			status.getErrorInfo().add(errorInfo);
			status.setStatusMessage(e.getMessage());
			return new ResponseEntity<Object>(status,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} */

		return new ResponseEntity<Object>(catalogData, HttpStatus.OK);

	}

	@RequestMapping(value = "/catalog/categories", method = RequestMethod.GET)
	public ResponseEntity<Object> handleGetCategoriesList(
			@RequestParam(required = false) String categoryList,
			HttpServletRequest servletRequest) {

		CatalogData catalogData = null;
/*		try {
			if (!GenericValidator.isBlankOrNull(categoryList)) {
				List<String> categories = new ArrayList<String>(
						StringUtils.commaDelimitedListToSet(categoryList));
				catalogData = new CatalogData();
				catalogData.setCategories(new Categories());
				for (String category : categories) {
					globalLoggingCatalog.getCategoryByName(category,
							catalogData.getCategories().getCategory());
				}
			} else {
				catalogData = globalLoggingCatalog.getCategories();
			}
		} catch (GLCatalogException e) {
			logger.error("Error While Retrieving Data", e);

			Status status = new Status();
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.setErrorCode("00000");
			errorInfo.setErrorMessage(e.getMessage());
			status.getErrorInfo().add(errorInfo);
			status.setStatusMessage(e.getMessage());
			return new ResponseEntity<Object>(status,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} */

		return new ResponseEntity<Object>(catalogData, HttpStatus.OK);

	}

	@RequestMapping(value = "/catalog/categories/{category}", method = RequestMethod.GET)
	public ResponseEntity<Object> handleGetCategory(
			@PathVariable("category") String category,
			HttpServletRequest servletRequest) throws ParseException {

		CatalogData catalogData = null;
	/*	try {
			catalogData = globalLoggingCatalog.getCategoryByName(category);
		} catch (GLCatalogException e) {
			logger.error("Error While Retrieving Data", e);

			Status status = new Status();
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.setErrorCode("00000");
			errorInfo.setErrorMessage(e.getMessage());
			status.getErrorInfo().add(errorInfo);
			status.setStatusMessage(e.getMessage());
			return new ResponseEntity<Object>(status,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} */

		return new ResponseEntity<Object>(catalogData, HttpStatus.OK);

	}

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

	private void getAttributeDetailsForEvents(CatalogData catalogData) {
	/*	for (Event _event : catalogData.getEvents().getEvent()) {
			if (_event != null && _event.getEventAttributes() != null) {

				List<String> eventAttributes = _event.getEventAttributes()
						.getAttribute();
				_event.setAttributes(new Attributes());
				for (String eventAttribute : eventAttributes) {
					globalLoggingCatalog.getAttributeByName(eventAttribute,
							_event.getAttributes().getAttribute());
				}
			}
		}*/
	}

	/**
	 * Sets the global log service.
	 *
	 * @param globalLoggingService
	 *            the global log service
	 */
/*	public void setGlobalLogCatalog(GlobalLoggingCatalog globalLoggingCatalog) {
		this.globalLoggingCatalog = globalLoggingCatalog;
	} */
}
