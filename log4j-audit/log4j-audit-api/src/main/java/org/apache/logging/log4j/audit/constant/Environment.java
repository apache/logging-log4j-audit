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
package org.apache.logging.log4j.audit.constant;

/**
 * Various environments applications run in.
 */
public enum Environment {

    PROD("Production"), PRE_PROD("Pre-Production"), UAT("UserAcceptance"), BETA("Beta"),
    RC("ReleaseCandidate"), STAGING("Staging"),
    DEV("Development"), DEVQA("DevelopmentQA"), QA("QA"), PERF("Performance");
    private final String name;

    private Environment(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static Environment getByName(String name) {
        for (Environment region : values()) {
            if (region.name.equals(name)) {
                return region;
            }
        }
        return null;
    }
}
