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
package org.apache.logging.log4j.catalog.jpa;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.DataType;
import org.apache.logging.log4j.catalog.jpa.config.ApplicationConfiguration;
import org.apache.logging.log4j.catalog.jpa.dao.AttributeRepository;
import org.apache.logging.log4j.catalog.jpa.dao.CategoryRepository;
import org.apache.logging.log4j.catalog.jpa.dao.EventRepository;
import org.apache.logging.log4j.catalog.jpa.model.AttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.EventAttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.service.CatalogService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {ApplicationConfiguration.class})
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/hsql/beforeTestRun.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/hsql/afterTestRun.sql")
})
@Repository
public class CatalogTest {

    @Autowired
    AttributeRepository attributeRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    private CatalogService catalogService;

    @BeforeClass
    public static void initTest() {
        System.setProperty("environment", "lab");
        System.setProperty("site", "dev1");
        System.setProperty("applicationName", "CatalogService");
        System.setProperty("spring.profiles.active", "eclipseLink");
    }

    @Test
    public void testRetrieveEvents() {
        List<EventModel> events = eventRepository.findAll();
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(4, events.size());
    }

    @Test
    public void testRetrieveEvent() {
        Optional<EventModel> optEvent = eventRepository.findOne(1L);
        assertTrue(optEvent.isPresent());
        EventModel event = optEvent.get();
        assertNotNull(event);
        assertEquals("login", event.getName());
    }

    @Test
    public void testAddAndUpdateEvent() {
        EventModel event = new EventModel();
        event.setName("test" + System.nanoTime());
        event.setDisplayName("Test -- " + System.nanoTime());
        event.setDescription("foo");

        AttributeModel attr = new AttributeModel();
        attr.setName("Attr1 -- " + System.nanoTime());
        attr.setDisplayName("Attribute #1 -- " + System.nanoTime());
        attr.setDescription("Test Attribute");
        attr.setDataType(DataType.BOOLEAN);
        attr.setIndexed(true);
        attr.setRequestContext(true);
        attr.setRequired(true);
        attr.setSortable(true);
        attr = attributeRepository.save(attr);
        AttributeModel attr2 = new AttributeModel();
        attr2.setName("Attr2 -- " + System.nanoTime());
        attr2.setDisplayName("Attribute #2 -- " + System.nanoTime());
        attr2.setDescription("Test Attribute Number 2");
        attr2.setDataType(DataType.BOOLEAN);
        attr2.setIndexed(false);
        attr2.setRequestContext(false);
        attr2.setRequired(false);
        attr2.setSortable(false);
        attr2 = attributeRepository.save(attr2);
        EventAttributeModel eventAttribute = new EventAttributeModel();
        eventAttribute.setAttribute(attr);
        eventAttribute.setEvent(event);
        eventAttribute.setRequired(true);
        event.addEventAttribute(eventAttribute);
        EventModel persisted = null;
        try {
            persisted = eventRepository.save(event);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull(persisted);
        assertNotNull(persisted.getId());
        assertEquals(event, persisted);
        assertNotNull(persisted.getAttributes());
        assertFalse(persisted.getAttributes().isEmpty());
        assertEquals(event.getAttributes().size(), persisted.getAttributes().size());
        eventAttribute = new EventAttributeModel();
        eventAttribute.setAttribute(attr2);
        eventAttribute.setEvent(persisted);
        eventAttribute.setRequired(false);
        persisted.addEventAttribute(eventAttribute);
        Long id = persisted.getId();
        try {
            persisted = eventRepository.save(event);
        } catch (Throwable ex) {
            ex.printStackTrace();
            fail();
        }
        assertNotNull(persisted);
        assertNotNull(persisted.getId());
        assertEquals(persisted.getId(), id);
        assertNotNull(persisted.getAttributes());
        assertFalse(persisted.getAttributes().isEmpty());
        assertEquals(event.getAttributes().size(), persisted.getAttributes().size());
    }

    @Test
    public void testModifyEvent() {

    }

    @Test
    public void testDeleteEvent() {

    }

    @Test
    public void testRetrieveAttributes() {
        List<AttributeModel> attributes = attributeRepository.findAll();
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());
        assertEquals(10, attributes.size());
    }

    @Test
    public void testRetrieveAttribute() {
        Optional<AttributeModel> optAttr = attributeRepository.findOne(1L);
        assertTrue(optAttr.isPresent());
        AttributeModel attr = optAttr.get();
        assertNotNull(attr);
        assertEquals("accountNumber", attr.getName());
    }

    @Test
    public void testAddAttribute() {

    }

    @Test
    public void testModifyAttribute() {
    }

    @Test
    public void testDeleteAttribute() {
    }

    @Test
    public void testRetrieveCatalog() {
        CatalogData data = catalogService.getCatalogData();
        assertNotNull("No catalog data was returned", data);
        assertEquals("Incorrect number of products", 1, data.getProducts().size());
        assertEquals("Incorrect number of events", 4, data.getEvents().size());
        assertEquals("Incorrect number of attributes", 10, data.getAttributes().size());
        assertEquals("Incorrect number of categories", 2, data.getCategories().size());
    }
}
