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
package org.apache.logging.log4j.audit.request;

import java.util.HashMap;
import java.util.Map;

public class RequestContextMappings {

    private final Map<String, RequestContextMapping> mappings = new HashMap<>();
    private final String headerPrefix;

    public RequestContextMappings(RequestContextMapping[] mappingArray, String headerPrefix) {
        this.headerPrefix = headerPrefix;
        for (RequestContextMapping mapping : mappingArray) {
            mappings.put(mapping.getFieldName().toLowerCase(), mapping);
        }
    }

    public RequestContextMapping getMapping(String name) {
        return mappings.get(name.toLowerCase());
    }

    public RequestContextMapping getMappingByHeader(String header) {
        String hdr = header.toLowerCase();
        if (hdr.startsWith(headerPrefix)) {
            return mappings.get(hdr.substring(headerPrefix.length()));
        }
        return null;
    }

    public String getHeaderPrefix() {
        return headerPrefix;
    }
}
