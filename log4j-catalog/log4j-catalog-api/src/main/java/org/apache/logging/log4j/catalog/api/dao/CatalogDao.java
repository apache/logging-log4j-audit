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
package org.apache.logging.log4j.catalog.api.dao;

import java.util.Map;

import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.CatalogWriter;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;

/**
 * Provides access to the Catalog.
 */
public interface CatalogDao extends CatalogReader, CatalogWriter {

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
     * Retrieves a Category. Modifications made to the Category may not be reflected if updateCategory() is
     * not called.
     * @param name The category name.
     * @return The Category.
     */
    Category getCategory(String name);


    Event getEvent(String name);


    Product getProduct(String name);

}
