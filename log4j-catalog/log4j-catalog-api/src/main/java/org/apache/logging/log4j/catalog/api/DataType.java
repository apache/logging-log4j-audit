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
package org.apache.logging.log4j.catalog.api;

/**
 * Mapping of attribute data types.
 */
public enum DataType {
    STRING("String", null),
    BIG_DECIMAL("BigDecimal", "java.math.BigDecimal"),
    DOUBLE("double", null),
    FLOAT("float", null),
    INT("int", null),
    LONG("long", null),
    BOOLEAN("boolean", null),
    LIST("List<String>", "java.util.List"),
    MAP("Map<String, String>", "java.util.Map");

    private final String typeName;

    public final String importClass;

    DataType(String typeName, String importClass) {
        this.typeName = typeName;
        this.importClass = importClass;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getImportClass() {
        return importClass;
    }

    public static DataType fromName(String typeName) {
        for (DataType dataType : DataType.values()) {
            if (dataType.typeName.equalsIgnoreCase(typeName)) {
                return dataType;
            }
        }
        throw new IllegalArgumentException("Unknown data type: " + typeName);
    }
}
