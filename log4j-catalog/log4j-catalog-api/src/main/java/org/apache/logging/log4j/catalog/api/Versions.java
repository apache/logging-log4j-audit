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
package org.apache.logging.log4j.catalog.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

public final class Versions {

    private static final String type = "application";
    private static final String subType = "vnd.apache.logging.log4j.audit+json";
    private static final String versionKey = "version";
    private static final String version1 = "1.0";
    private static final Map<String, String> V1Parameters = new HashMap<>();
    static {
        V1Parameters.put(versionKey, version1);
    }
    public static final MediaType V1_0 = new MediaType(type, subType, V1Parameters);
    public static final String V1_0_VALUE = type + "/" + subType + "; " + versionKey + "=\"" + version1 +"\"";

    private Versions() { }
}
