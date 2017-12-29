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

import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.Event;

import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.catalog.api.constant.Constants.DEFAULT_CATALOG;

/**
 *
 */
public interface CatalogManager {

    default Event getEvent(String eventName) {
        return getEvent(eventName, DEFAULT_CATALOG);
    }

    Event getEvent(String eventName, String catalogId);

    default List<String> getRequiredContextAttributes(String eventName) {
        return getRequiredContextAttributes(eventName, DEFAULT_CATALOG);
    }

    List<String> getRequiredContextAttributes(String eventName, String catalogId);

    default List<String> getAttributeNames(String eventName) {
        return getAttributeNames(eventName, DEFAULT_CATALOG);
    }

    List<String> getAttributeNames(String eventName, String catalogId);

    default Map<String, Attribute> getAttributes(String eventName) {
        return getAttributes(eventName, DEFAULT_CATALOG);
    }

    Map<String, Attribute> getAttributes(String eventName, String catalogId);

    Map<String, Attribute> getRequestContextAttributes();

    default Attribute getAttribute(String attributeName) {
        return getAttribute(attributeName, DEFAULT_CATALOG);
    }

    Attribute getAttribute(String attributeName, String catalogId);
}
