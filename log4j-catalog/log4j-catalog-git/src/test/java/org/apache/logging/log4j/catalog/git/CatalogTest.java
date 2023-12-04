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
package org.apache.logging.log4j.catalog.git;

import static org.junit.Assert.*;

import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.dao.CatalogDao;
import org.apache.logging.log4j.catalog.git.config.ApplicationConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {ApplicationConfiguration.class})
@ActiveProfiles("lab")
public class CatalogTest {

    @Autowired
    private CatalogDao catalogDao;

    @BeforeClass
    public static void initTest() {
        System.setProperty("environment", "lab");
        System.setProperty("site", "dev1");
        System.setProperty("applicationName", "CatalogService");
    }

    @Test
    public void testRetrieveCatalog() {
        CatalogData data = catalogDao.read();
        assertNotNull("No catalog data was returned", data);
        assertEquals("Incorrect number of products", 1, data.getProducts().size());
        assertEquals("Incorrect number of events", 4, data.getEvents().size());
        assertEquals("Incorrect number of attributes", 10, data.getAttributes().size());
        assertEquals("Incorrect number of categories", 2, data.getCategories().size());
    }

    @Test
    public void testRetrieveEvents() {}

    @Test
    public void testRetrieveEvent() {}

    @Test
    public void testAddEvent() {}

    @Test
    public void testModifyEvent() {}

    @Test
    public void testDeleteEvent() {}

    @Test
    public void testRetrieveAttributes() {}

    @Test
    public void testRetrieveAttribute() {}

    @Test
    public void testAddAttribute() {}

    @Test
    public void testModifyAttribute() {}

    @Test
    public void testDeleteAttribute() {}
}
