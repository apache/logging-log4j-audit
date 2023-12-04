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
package org.apache.logging.log4j.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.catalog.CatalogManager;
import org.apache.logging.log4j.audit.catalog.CatalogManagerImpl;
import org.apache.logging.log4j.audit.catalog.StringCatalogReader;
import org.apache.logging.log4j.audit.exception.ConstraintValidationException;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.dao.ClassPathCatalogReader;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class AuditLoggerTest {

    private static CatalogReader catalogReader;
    private static ListAppender app;

    private AbstractEventLogger auditLogger;

    @BeforeClass
    public static void setupClass() throws Exception {
        catalogReader = new StringCatalogReader();
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        for (Map.Entry<String, Appender> entry : config.getAppenders().entrySet()) {
            if (entry.getKey().equals("List")) {
                app = (ListAppender) entry.getValue();
                break;
            }
        }
        assertNotNull("No Appender", app);
    }

    private AbstractEventLogger buildAuditLogger(CatalogReader catalogReader) {
        CatalogManager catalogManager = new CatalogManagerImpl(catalogReader);
        AuditLogger auditLogger = new AuditLogger();
        auditLogger.setCatalogManager(catalogManager);
        return auditLogger;
    }

    @Before
    public void before() {
        app.clear();
        ThreadContext.clearMap();
    }

    @Test
    public void testAuditLogger() {
        auditLogger = buildAuditLogger(catalogReader);

        ThreadContext.put("accountNumber", "12345");
        ThreadContext.put("companyId", "12345");
        ThreadContext.put("userId", "JohnDoe");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        Map<String, String> properties = new HashMap<>();
        properties.put("toAccount", "123456");
        properties.put("fromAccount", "111111");
        properties.put("amount", "111.55");
        try {
            auditLogger.logEvent("transfer", properties);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        List<String> msgs = app.getMessages();
        assertNotNull("No messages", msgs);
        assertEquals("No messages", 1, msgs.size());
        String msg = msgs.get(0);
        assertTrue("No companyId", msg.contains("companyId=\"12345\""));
        assertTrue("No ipAddress", msg.contains("ipAddress=\"127.0.0.1\""));
        assertTrue("No toAccount", msg.contains("toAccount=\"123456\""));
    }

    @Test(expected = ConstraintValidationException.class)
    public void testMissingRequestContextAttribute() {
        auditLogger = buildAuditLogger(catalogReader);

        Map<String, String> properties = new HashMap<>();
        properties.put("toAccount", "123456");
        properties.put("fromAccount", "111111");
        properties.put("amount", "111.55");
        auditLogger.logEvent("transfer", properties);
    }

    @Test(expected = ConstraintValidationException.class)
    public void testMissingEventAttribute() {
        auditLogger = buildAuditLogger(catalogReader);

        ThreadContext.put("companyId", "12345");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        Map<String, String> properties = new HashMap<>();
        properties.put("toAccount", "123456");
        properties.put("amount", "111.55");
        auditLogger.logEvent("transfer", properties);
    }

    @Test
    public void testAuditLoggerWithBasicCatalog() throws Exception {
        auditLogger = buildAuditLogger(
                new ClassPathCatalogReader(Collections.singletonMap("catalogFile", "basicCatalog.json")));

        auditLogger.logEvent("login", null);
    }
}
