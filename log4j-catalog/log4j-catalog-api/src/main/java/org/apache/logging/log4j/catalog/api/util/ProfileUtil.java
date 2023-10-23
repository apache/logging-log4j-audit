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
package org.apache.logging.log4j.catalog.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

/**
 * Sets the profile for the application if it hasn't already been set.
 */
public final class ProfileUtil {

    private static final String SPRING_PROFILE = "spring.profiles.active";

    private ProfileUtil() {
    }

    /**
     * Set the active profile if none has been specified.
     * @param servletContext
     */
    public static void setActiveProfile(ServletContext servletContext) {
        String springProfile = System.getProperty(SPRING_PROFILE);
        if (springProfile == null) {
            springProfile = System.getenv(SPRING_PROFILE);
        }
        if (springProfile == null) {
            springProfile = servletContext.getInitParameter(SPRING_PROFILE);
        }
        if (springProfile == null) {
            Properties props = loadProperties(servletContext);
            String activeProfile = props.getProperty(SPRING_PROFILE);
            if (activeProfile == null) {
                servletContext.setInitParameter(SPRING_PROFILE, "eclipseLink");
            }
        }
    }

    private static Properties loadProperties(ServletContext servletContext) {
        Properties props = new Properties();
        String env = System.getProperty("env");
        if (env == null) {
            env = System.getenv("env");
        }
        StringBuilder sb = new StringBuilder("catalog-");
        if (env != null) {
            sb.append(env);
        }
        sb.append("config.properties");
        InputStream is = ProfileUtil.class.getClassLoader().getResourceAsStream(sb.toString());
        if (is != null) {
            try {
                props.load(is);
            } catch (IOException ioe) {
                servletContext.log("Unable to load " + sb.toString() + ": " + ioe.getMessage());
            }
        }
        return props;
    }
}
