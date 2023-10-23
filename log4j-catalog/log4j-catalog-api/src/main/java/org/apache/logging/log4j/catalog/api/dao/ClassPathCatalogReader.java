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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.util.LoaderUtil;

/**
 * Reads the catalog from the local file system.
 */
public class ClassPathCatalogReader extends AbstractCatalogReader {

    private static final Logger LOGGER = LogManager.getLogger(ClassPathCatalogReader.class);

    private static final String CATALOG_ATTRIBUTE_NAME = "catalogFile";
    private static final String DEFAULT_CATALOG_FILE = "catalog.json";

    private final String catalog;
    private final LocalDateTime lastUpdated;

    public ClassPathCatalogReader(Map<String, String> attributes) throws IOException {
        String catalogFile = attributes != null ?
            attributes.getOrDefault(CATALOG_ATTRIBUTE_NAME, DEFAULT_CATALOG_FILE) : DEFAULT_CATALOG_FILE;
        Collection<URL> catalogs = LoaderUtil.findResources(catalogFile);
        if (catalogs.isEmpty()) {
            LOGGER.error("No catalog named {} could be found on the class path", catalogFile);
            throw new FileNotFoundException("No catalog named " + catalogFile + " could be found");
        }

        URL catalogURL = catalogs.iterator().next();
        if (catalogs.size() > 1) {
            LOGGER.warn("Multiple catalogs named {} were found. Using {}", catalogFile, catalogURL.toString());
        }

        catalog = readCatalog(catalogURL);
        LocalDateTime localDateTime = null;
        try {
            URLConnection connection = catalogURL.openConnection();
            localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(connection.getLastModified()),
                    ZoneId.systemDefault());
        } catch (IOException ioe) {
            LOGGER.warn("Unable to open connection to {}", catalogURL.toString());
        }
        lastUpdated = localDateTime;
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper objectMapper = new ObjectMapper(factory);
        catalogData = objectMapper.readValue(catalog, CatalogData.class);
    }

    private String readCatalog(URL catalogUrl) throws IOException {
        try (InputStream is = catalogUrl.openStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString("UTF-8");
        }
    }

    public ClassPathCatalogReader() throws IOException {
        this(null);
    }

    @Override
    public String readCatalog() {
        return catalog;
    }

    @Override
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
