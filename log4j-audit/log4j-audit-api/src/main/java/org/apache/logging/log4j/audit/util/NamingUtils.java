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
package org.apache.logging.log4j.audit.util;

public final class NamingUtils {

    private NamingUtils() {
    }

    public static String getPackageName(String className) {
        return className.substring(0, className.lastIndexOf('.'));
    }

    public static String getSimpleName(String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static String getMethodShortName(String name) {
        return name.replaceFirst("(get|set|is|has)", "");
    }

    public static String upperFirst(String name) {
        return String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
    }

    public static String lowerFirst(String name) {
        return String.valueOf(name.charAt(0)).toLowerCase() + name.substring(1);
    }

    public static String getSetterName(String fieldName) {
        return "set" + upperFirst(fieldName);
    }

    public static String getGetterName(String fieldName, String type) {
        if ("boolean".equals(type)) {
            return "is" + upperFirst(fieldName);
        } else {
            return "get" + upperFirst(fieldName);
        }
    }

    public static String getClassName(String className) {
        return upperFirst(className.replaceAll("[^a-zA-Z0-9_]+", ""));
    }

    public static String getFieldName(String fieldName) {
        return fieldName.replaceAll("[^a-zA-Z0-9_]+", "");
    }

    public static String methodCaseName(String variable) {
        return variable.substring(0, 1).toUpperCase() + variable.substring(1);
    }

    public static String getAccessorName(String type, String methodName) {
        String prefix = "get";
        if (type.equals("boolean")) {
            prefix = "is";
        }
        return prefix + methodCaseName(methodName);
    }

    public static String getMutatorName(String methodName) {
        return "set" + methodCaseName(methodName);
    }
}
