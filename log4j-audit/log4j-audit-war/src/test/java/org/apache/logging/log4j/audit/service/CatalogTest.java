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
package org.apache.logging.log4j.audit.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.logging.log4j.audit.service.config.WebMvcAppContext;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.EventAttribute;
import org.apache.logging.log4j.catalog.api.Versions;
import org.apache.logging.log4j.catalog.api.util.CatalogEventFilter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebMvcAppContext.class}, loader=AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class CatalogTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeClass
    public static void setupClass() {
        System.setProperty("spring.profiles.active", "eclipseLink");
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testCatalogAPI() throws Exception {

        String result = mockMvc.perform(
                get("/catalog/TEST/attributes")
                        .accept(Versions.V1_0))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        assertNotNull("No result returned for TEST catalog", result);
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType typeReference =
                TypeFactory.defaultInstance().constructCollectionType(List.class, Attribute.class);
        List<Attribute> attributes = objectMapper.readValue(result, typeReference);
        assertNotNull("Result is not a list", attributes);
        assertEquals("Incorrect number of attributes in list", 0, attributes.size());
        result = mockMvc.perform(
                get("/catalog/DEFAULT/attributes")
                .accept(Versions.V1_0))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        assertNotNull("No result returned for DEFAULT catalog", result);

        attributes = objectMapper.readValue(result, typeReference);
        assertNotNull("Result is not a list", attributes);
        assertEquals("Incorrect number of attributes in list", 10, attributes.size());
        result = mockMvc.perform( get("/catalog/DEFAULT/events").accept(Versions.V1_0))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        assertNotNull("No result returned for DEFAULT catalog", result);
        typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class, Event.class);
        List<Event> events = objectMapper.readValue(result, typeReference);
        assertNotNull("Result is not a list", events);
        assertEquals("Incorrect number of events in list", 4, events.size());
        Event event = new Event();
        event.setName("createUser");
        event.setDescription("Create a User");
        event.setCatalogId("TEST");
        event.setDisplayName("Create User");
        List<EventAttribute> eventAttributes = new ArrayList<>();
        EventAttribute eventAttribute = new EventAttribute();
        eventAttribute.setName("loginId");
        eventAttribute.setRequired(true);
        eventAttributes.add(eventAttribute);
        eventAttribute = new EventAttribute();
        eventAttribute.setName("userId");
        eventAttribute.setRequired(true);
        eventAttributes.add(eventAttribute);
        event.setAttributes(eventAttributes);
        ObjectMapper mapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("catalogEvent", new CatalogEventFilter());
        mapper.setFilterProvider(filterProvider);
        String json = mapper.writeValueAsString(event);
        result = mockMvc.perform(post("/catalog/event").content(json).accept(Versions.V1_0).contentType(Versions.V1_0))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn().getResponse().getContentAsString();
        assertNotNull("No content returned from create user", result);
        assertTrue("Missing catalog id", result.contains("\"catalogId\":\"TEST\""));
    }
}
