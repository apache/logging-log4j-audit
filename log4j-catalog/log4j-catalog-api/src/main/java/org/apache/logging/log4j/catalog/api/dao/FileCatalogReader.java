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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.CatalogData;

/**
 * Reads the catalog from the local file system.
 */
public class FileCatalogReader extends AbstractCatalogReader {

    private static final Logger LOGGER = LogManager.getLogger(FileCatalogReader.class);
    private static final String BASEDIR = "baseDir";

    private static final String CATALOG_ATTRIBUTE_NAME = "catalogFile";
    private static final String DEFAULT_CATALOG_FILE = "src/main/resources/catalog.json";

    private final String catalog;
    private LocalDateTime lastUpdated;

    public FileCatalogReader(Map<String, String> attributes) throws IOException {
        StringBuilder catalogPath = new StringBuilder();
        String basePath = attributes.get(BASEDIR);
        String catalogFile = attributes.getOrDefault(CATALOG_ATTRIBUTE_NAME, DEFAULT_CATALOG_FILE);
        if (basePath != null) {
            catalogPath.append(attributes.get(BASEDIR));
            if (basePath.endsWith("/")) {
                if (catalogFile.startsWith("/")) {
                    catalogPath.append(catalogFile.substring(1));
                } else {
                    catalogPath.append(catalogFile);
                }
            } else {
                if (catalogFile.startsWith("/")) {
                    catalogPath.append(catalogFile);
                } else {
                    catalogPath.append("/").append(catalogFile);
                }
            }
        } else if (catalogFile != null){
            catalogPath.append(catalogFile);
        } else {
            LOGGER.warn("No catalogFile attribute was provided. Using {}", DEFAULT_CATALOG_FILE);
            catalogPath.append(DEFAULT_CATALOG_FILE);
        }
        Path path = Paths.get(catalogPath.toString());
        lastUpdated = LocalDateTime.ofInstant(Instant.ofEpochMilli(path.toFile().lastModified()),
                ZoneId.systemDefault());
        byte[] encoded = Files.readAllBytes(path);
        catalog = new String(encoded, StandardCharsets.UTF_8);
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper objectMapper = new ObjectMapper(factory);
        catalogData = objectMapper.readValue(catalog, CatalogData.class);
    }

    public FileCatalogReader() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(DEFAULT_CATALOG_FILE));
        catalog = new String(encoded, StandardCharsets.UTF_8);
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper objectMapper = new ObjectMapper(factory);
        catalogData = objectMapper.readValue(catalog, CatalogData.class);
    }

    @Override
    public String readCatalog() {
        return catalog;
    }

    @Override
    public LocalDateTime getLastUpdated() {
        return null;
    }
}
