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
package org.apache.logging.log4j.catalog.api.dao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogReader;

/**
 * Provides access to the JSON version of the catalog. This version is not modifiable.
 */
//@Component
public class JsonCatalogReader extends AbstractCatalogReader {

    private static final Logger LOGGER = LogManager.getLogger(JsonCatalogReader.class);

    //@Autowired
    CatalogReader catalogReader;

    public CatalogReader getCatalogReader() {
        return catalogReader;
    }

    public void setCatalogReader(CatalogReader catalogReader) {
        this.catalogReader = catalogReader;
    }

    //@PostConstruct
    public void init() {
        catalogData = catalogReader.read();
        for (Attribute attribute : catalogData.getAttributes()) {
            attributes.put(attribute.getName(), attribute);
        }
    }

    @Override
    public String readCatalog() {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper(factory);
        try {
            return mapper.writeValueAsString(catalogData);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Unable to serialze Catalog", ex);
            return null;
        }
    }

    @Override
    public LocalDateTime getLastUpdated() {
        return catalogReader.getLastUpdated();
    }
}
