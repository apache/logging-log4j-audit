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
package org.apache.logging.log4j.audit;


import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.catalog.CatalogManager;
import org.apache.logging.log4j.audit.catalog.CatalogManagerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.audit.catalog.StringCatalogReader;
import org.apache.logging.log4j.audit.exception.AuditException;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class AuditLoggerTest {

    private static AbstractEventLogger auditLogger;

    private static CatalogManager catalogManager;

    private static LoggerContext ctx;
    private static ListAppender app;

    @BeforeClass
    public static void setupClass() throws Exception {
        catalogManager = new CatalogManagerImpl(new StringCatalogReader());
        auditLogger = new AuditLogger();
        auditLogger.setCatalogManager(catalogManager);
        ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        for (Map.Entry<String, Appender> entry : config.getAppenders().entrySet()) {
            if (entry.getKey().equals("List")) {
                app = (ListAppender) entry.getValue();
                break;
            }
        }
        assertNotNull("No Appender", app);
    }

    @Before
    public void before() {
        app.clear();
    }

    @Test
    public void testAuditLogger() {
        ThreadContext.put("companyId", "12345");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        Map<String, String> properties = new HashMap<String, String>();
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
        assertTrue("No messages", msgs.size() == 1);
        String msg = msgs.get(0);
        assertTrue("No companyId", msg.contains("companyId=\"12345\""));
        assertTrue("No ipAddress", msg.contains("ipAddress=\"127.0.0.1\""));
        assertTrue("No toAccount", msg.contains("toAccount=\"123456\""));
    }

    @Test(expected = AuditException.class)
    public void testBadAttribute() {
        ThreadContext.put("companyId", "12345");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("toAccount", "123456");
        properties.put("amount", "111.55");
        auditLogger.logEvent("transfer", properties);
    }
}
