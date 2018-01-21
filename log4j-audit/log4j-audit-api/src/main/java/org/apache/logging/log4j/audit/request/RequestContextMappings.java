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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.audit.annotation.Chained;
import org.apache.logging.log4j.audit.annotation.ChainedSupplier;
import org.apache.logging.log4j.audit.annotation.ClientServer;
import org.apache.logging.log4j.audit.annotation.HeaderPrefix;
import org.apache.logging.log4j.audit.annotation.Local;

public class RequestContextMappings {

    private static final String DEFAULT_HEADER_PREFIX = "request-context-";
    private final Map<String, RequestContextMapping> mappings = new HashMap<>();
    private final String headerPrefix;

    public RequestContextMappings(String fqcn) {
        this(getClass(fqcn));
    }

    private static Class<?> getClass(String fqcn) {
        if (fqcn == null) {
            throw new IllegalArgumentException("RequestContext class name cannot be null");
        }
        try {
            return Class.forName(fqcn);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Invalid RequestContext class name", ex);
        }
    }

    public RequestContextMappings(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("A RequestContext class must be provided");
        }
        Annotation annotation = clazz.getAnnotation(HeaderPrefix.class);
        this.headerPrefix = annotation != null ? ((HeaderPrefix) annotation).value().toLowerCase() : DEFAULT_HEADER_PREFIX;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                String fieldName;
                try {
                    fieldName = (String) field.get(null);
                } catch (IllegalAccessException ex) {
                    continue;
                }
                if (fieldName == null) {
                    continue;
                }
                annotation = field.getAnnotation(ClientServer.class);
                if (annotation != null) {
                    mappings.put(fieldName.toLowerCase(), new ClientServerMapping(fieldName));
                    continue;
                }

                annotation = field.getAnnotation(Local.class);
                if (annotation != null) {
                    mappings.put(fieldName.toLowerCase(), new LocalMapping(fieldName));
                }
            } else if (field.getType().equals(Supplier.class)) {
                annotation = field.getAnnotation(Chained.class);
                if (annotation != null) {
                    Chained chained = (Chained) annotation;
                    try {
                        @SuppressWarnings("unchecked")
                        Supplier<String> supplier = (Supplier<String>) field.get(null);
                        mappings.put(chained.fieldName().toLowerCase(),
                                new ChainedMapping(chained.fieldName(), chained.chainedFieldName(), supplier));
                    } catch (IllegalAccessException ex) {
                        throw new IllegalArgumentException("Unable to retrieve Supplier for chained field " + chained.fieldName());
                    }
                }
            }
        }
        mappings.entrySet().removeIf(a -> validateChained(a.getValue()));
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

    private boolean validateChained(RequestContextMapping mapping) {
        return mapping.getScope() == Scope.CHAIN && !mappings.containsKey(mapping.getChainKey().toLowerCase());
    }
}
