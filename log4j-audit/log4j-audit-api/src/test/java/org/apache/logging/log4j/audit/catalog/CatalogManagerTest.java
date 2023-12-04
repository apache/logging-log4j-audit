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
package org.apache.logging.log4j.audit.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.catalog.api.Event;
import org.junit.Test;

/**
 *
 */
public class CatalogManagerTest {
    @Test
    public void testCatalog() throws Exception {
        CatalogManager manager = new CatalogManagerImpl(new StringCatalogReader());
        Event event = manager.getEvent("transfer");
        assertNotNull("No transfer event", event);
        assertEquals(
                "{\"name\" : \"transfer\", \"displayName\" : \"Transfer\", \"description\" : \"Transfer between accounts\", "
                        + "\"attributes\" : [{\"name\" : \"toAccount\", \"required\" : true}, {\"name\" : \"fromAccount\", \"required\" : true}, {\"name\" : \"amount\", \"required\" : true}]}",
                event.toString());

        Event login = manager.getEvent("login");
        assertNotNull("No login event", login);
        assertEquals(
                "{\"name\" : \"login\", \"displayName\" : \"Login\", \"description\" : \"User Login\", \"attributes\" : []}",
                login.toString());
    }
}
