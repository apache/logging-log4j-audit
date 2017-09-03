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
package org.apache.logging.log4j.audit.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.audit.exception.AuditException;
import org.apache.logging.log4j.audit.util.NamingUtils;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.EventAttribute;

/**
 *
 */
public class CatalogManagerImpl implements CatalogManager {

    private static Logger logger = LogManager.getLogger(CatalogManagerImpl.class);

    private volatile Map<String, CatalogInfo> infoMap;

    private Map<String, Attribute> requestContextAttributes = new HashMap<>();

    private final Map<String, Attribute> attributeMap = new HashMap<>();

    private static final String REQCTX = "ReqCtx_";

    public CatalogManagerImpl(CatalogReader catalogReader) {
        try {
            infoMap = initializeData(catalogReader);
        } catch (Exception ex) {
            throw new AuditException("Unable to initialize catalog data", ex);
        }
    }

    @Override
    public Event getEvent(String eventName) {
        CatalogInfo info = infoMap.get(eventName);
        return info != null ? info.event : null;
    }

    @Override
    public List<String> getRequiredContextAttributes(String eventName) {
        return infoMap.get(eventName).requiredContextAttributes;
    }

    @Override
    public Map<String, Attribute> getAttributes(String eventName) {
        return infoMap.get(eventName).attributes;
    }

    @Override
    public List<String> getAttributeNames(String eventName) {
        return infoMap.get(eventName).attributeNames;
    }

    @Override
    public Attribute getAttribute(String name) {
        return attributeMap.get(name);
    }

    @Override
    public Map<String, Attribute> getRequestContextAttributes() {
        return requestContextAttributes;
    }

    private Map<String, CatalogInfo> initializeData(CatalogReader catalogReader) throws Exception {
        String catalog = catalogReader.readCatalog();
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper(factory);
        CatalogData data = mapper.readValue(catalog, CatalogData.class);
        for (Attribute attr : data.getAttributes()) {
            if (attr.isRequestContext()) {
                requestContextAttributes.put(attr.getName(), attr);
            }
            attributeMap.put(attr.getName(), attr);
        }
        Map<String, CatalogInfo> map = new HashMap<>(data.getEvents().size());
        for (Event event : data.getEvents()) {
            CatalogInfo info = new CatalogInfo();
            info.event = event;
            List<String> required = new ArrayList<>();
            List<String> names = new ArrayList<>();
            info.attributes = new HashMap<>(names.size());
            if (event.getAttributes() != null) {
                for (EventAttribute eventAttribute : event.getAttributes()) {
                    String name = eventAttribute.getName();
                    Attribute attribute = attributeMap.get(name);
                    info.attributes.put(name, attribute);
                    if (name.indexOf('.') != -1) {
                        name = name.replaceAll("\\.", "");
                    }
                    if (name.indexOf('/') != -1) {
                        name = name.replaceAll("/", "");
                    }
                    if (attribute.isRequestContext()) {
                        if (attribute.isRequired()) {
                            if (name.startsWith(REQCTX)) {
                                name = name.substring(REQCTX.length());
                            }
                            required.add(name);
                        }
                    } else {
                        names.add(name);
                    }
                }
            }
            info.requiredContextAttributes = required;
            info.attributeNames = names;
            map.put(NamingUtils.getFieldName(event.getName()), info);
        }
        return map;
    }

    private class CatalogInfo {
        private Event event;

        private List<String> requiredContextAttributes;

        private List<String> attributeNames;

        private Map<String, Attribute> attributes;
    }
}
