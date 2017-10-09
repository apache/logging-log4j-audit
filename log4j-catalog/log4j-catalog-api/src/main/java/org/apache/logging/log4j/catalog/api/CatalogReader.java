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
package org.apache.logging.log4j.catalog.api;

import java.time.LocalDateTime;
import java.util.Map;

/**
 *
 */
public interface CatalogReader {

    /**
     * Returns the Catalog object.
     * @return the Catalog.
     */
    CatalogData read();

    /**
     * Returns a String representation of the Catalog. The representation format is implementation specific.
     * @return a String containing the Catalog data.
     */
    String readCatalog();

    /**
     * Return all the Attributes as a Map.
     * @return A map of the attributes where the key is the Attribute's name.
     */
    Map<String, Attribute> getAttributes();

    /**
     * Retrieves an Attribute.
     * @param name The attribute name.
     * @return The Attribute or null if no attribute with the specified name exists.
     */
    Attribute getAttribute(String name);

    /**
     * Retrieves a Category.
     * not called.
     * @param name The category name.
     * @return The Category.
     */
    Category getCategory(String name);

    /**
     *
     * @param name
     * @return
     */
    Event getEvent(String name);

    /**
     *
     * @param name
     * @return
     */
    Product getProduct(String name);

    /**
     * Returns the last update time of the catalog.
     * @return The last update time of the catalog.
     */
    LocalDateTime getLastUpdated();
}
