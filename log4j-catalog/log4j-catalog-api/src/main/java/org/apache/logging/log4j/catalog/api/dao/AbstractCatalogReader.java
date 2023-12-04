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

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;

public abstract class AbstractCatalogReader implements CatalogReader {
    protected CatalogData catalogData = null;

    protected final Map<String, Attribute> attributes = new HashMap<>();

    @Override
    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public Attribute getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Category getCategory(String name) {
        if (catalogData.getCategories() != null) {
            return catalogData.getCategories().stream()
                    .filter(c -> c.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Event getEvent(String name) {
        if (catalogData.getEvents() != null) {
            return catalogData.getEvents().stream()
                    .filter(e -> e.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Product getProduct(String name) {
        if (catalogData.getProducts() != null) {
            return catalogData.getProducts().stream()
                    .filter(p -> p.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public String readCatalog() {
        return null;
    }

    @Override
    public CatalogData read() {
        return catalogData;
    }
}
