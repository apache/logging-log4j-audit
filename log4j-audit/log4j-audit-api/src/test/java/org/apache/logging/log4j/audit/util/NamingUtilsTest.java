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
package org.apache.logging.log4j.audit.util;

import static org.apache.logging.log4j.audit.util.NamingUtils.getMethodShortName;
import static org.apache.logging.log4j.audit.util.NamingUtils.getPackageName;
import static org.apache.logging.log4j.audit.util.NamingUtils.getSimpleName;
import static org.apache.logging.log4j.audit.util.NamingUtils.lowerFirst;

/**
 *
 */
public class NamingUtilsTest {

    public static void main(String[] args) {
        String blah = "com.test.generator.Classname";
        System.out.println(getSimpleName(blah));
        System.out.println(lowerFirst(getSimpleName(blah)));

        System.out.println(getPackageName(blah));

        System.out.println(getMethodShortName("getName"));
        System.out.println(getMethodShortName("setName"));
    }
}
