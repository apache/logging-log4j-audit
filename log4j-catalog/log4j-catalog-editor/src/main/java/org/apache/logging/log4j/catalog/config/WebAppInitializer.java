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
package org.apache.logging.log4j.catalog.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.util.ProfileUtil;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;

public class WebAppInitializer {
    private static final String APPLICATION_NAME = "AuditCatalog";
    private static Logger LOGGER = LogManager.getLogger(WebAppInitializer.class);

    @Bean
    public ServletContextInitializer initializer() {
        return servletContext -> {
            LOGGER.info("Starting Audit Catalog Editor");
            servletContext.setInitParameter("applicationName", APPLICATION_NAME);
            ProfileUtil.setActiveProfile(servletContext);
            servletContext.setInitParameter("isEmbedded", "true");
            System.setProperty("applicationName", APPLICATION_NAME);
            //AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
            //rootContext.setDisplayName(APPLICATION_NAME);
            //rootContext.register(WebMvcAppContext.class);
            //servletContext.addListener(new ContextLoaderListener(rootContext));

            //ServletRegistration.Dynamic restServlet = servletContext.addServlet("dispatcherServlet", new DispatcherServlet(rootContext));
            //restServlet.setLoadOnStartup(1);
            //restServlet.addMapping("/*");
        };
    }
}
