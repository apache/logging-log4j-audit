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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.event.Transfer;
import org.apache.logging.log4j.audit.exception.AuditException;
import org.apache.logging.log4j.audit.exception.ConstraintValidationException;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.test.appender.AlwaysFailAppender;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 */
public class TransferTest extends BaseEventTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final String failingAppenderName = "failingAppenderName";

    @After
    public void cleanup() {
	    LogEventFactory.resetDefaultHandler();
    }

    @Test
    public void testValidationFailureForInvalidRequestContextAttribute() {
        MutableBoolean exceptionHandled = new MutableBoolean(false);
        LogEventFactory.setDefaultHandler((message, ex) -> {
            assertThat(ex, instanceOf(ConstraintValidationException.class));
            exceptionHandled.setTrue();
        });

        Transfer transfer = LogEventFactory.getEvent(Transfer.class);
        transfer.setToAccount(0);

        assertTrue("Should have thrown a ConstraintValidationException", exceptionHandled.isTrue());
    }

    @Test
    public void testValidationFailureForMissingRequestContextAttribute() {
	    MutableBoolean exceptionHandled = new MutableBoolean(false);
	    LogEventFactory.setDefaultHandler((message, ex) -> {
		    assertThat(ex, instanceOf(ConstraintValidationException.class));
		    exceptionHandled.setTrue();
	    });

        Transfer transfer = LogEventFactory.getEvent(Transfer.class);
        ThreadContext.put("companyId", "12345");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        transfer.setToAccount(123456);
        transfer.setFromAccount(111111);
        transfer.setAmount(new BigDecimal(111.55));
        transfer.logEvent();

	    assertTrue("Should have thrown a ConstraintValidationException", exceptionHandled.isTrue());
    }

    @Test
    public void testValidationFailureForMissingEventAttribute() {
	    MutableBoolean exceptionHandled = new MutableBoolean(false);
	    LogEventFactory.setDefaultHandler((message, ex) -> {
		    assertThat(ex, instanceOf(ConstraintValidationException.class));
		    exceptionHandled.setTrue();
	    });

        Transfer transfer = LogEventFactory.getEvent(Transfer.class);
        ThreadContext.put("accountNumber", "12345");
        ThreadContext.put("companyId", "12345");
        ThreadContext.put("userId", "JohnDoe");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        transfer.setToAccount(123456);
        transfer.setFromAccount(111111);
        transfer.logEvent();

	    assertTrue("Should have thrown a ConstraintValidationException", exceptionHandled.isTrue());
    }

    @Test
    public void testAuditClass() {
        Transfer transfer = LogEventFactory.getEvent(Transfer.class);
        ThreadContext.put("accountNumber", "12345");
        ThreadContext.put("companyId", "12345");
        ThreadContext.put("userId", "JohnDoe");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        transfer.setToAccount(123456);
        transfer.setFromAccount(111111);
        transfer.setAmount(new BigDecimal(111.55));
        try {
            transfer.logEvent();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        transfer.setCompletionStatus("Success");
        try {
            transfer.logEvent();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        List<String> msgs = app.getMessages();
        assertNotNull("No messages", msgs);
        assertEquals("No messages", 2, msgs.size());
        String msg = msgs.get(0);
        assertTrue("No companyId", msg.contains("companyId=\"12345\""));
        assertTrue("No ipAddress", msg.contains("ipAddress=\"127.0.0.1\""));
        assertTrue("No toAccount", msg.contains("toAccount=\"123456\""));
    }

    private AbstractConfiguration setUpFailingAppender() {
        Logger auditLogger = (Logger) LogManager.getContext(false).getLogger("AuditLogger");
        AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();

        Appender appender = AlwaysFailAppender.createAppender(failingAppenderName);
        appender.start();
        config.addLoggerAppender(auditLogger, appender);

        return config;
    }

    private Transfer setUpMinimumEvent() {
        ThreadContext.put("accountNumber", "12345");
        ThreadContext.put("userId", "JohnDoe");
        ThreadContext.put("loginId", "TestUser");

        Transfer transfer = LogEventFactory.getEvent(Transfer.class);
        transfer.setToAccount(123456);
        transfer.setFromAccount(111111);
        transfer.setAmount(new BigDecimal(111.55));
        return transfer;
    }

    @Test
    public void testDefaultExceptionHandlerIsInvokedOnEventLogFailure() {
        AbstractConfiguration config = setUpFailingAppender();

        exception.expect(AuditException.class);
        exception.expectCause(isA(LoggingException.class));
        exception.expectMessage("Error logging event transfer");

        Transfer transfer = setUpMinimumEvent();
        try {
            transfer.logEvent();
        } finally {
            config.removeAppender(failingAppenderName);
        }
    }

    @Test
    public void testCustomExceptionHandlerIsPassedToEvent() {
        AbstractConfiguration config = setUpFailingAppender();

        MutableBoolean exceptionHandled = new MutableBoolean(false);
	    LogEventFactory.setDefaultHandler((message, ex) -> {
	        assertThat(ex, instanceOf(LoggingException.class));
	        exceptionHandled.setTrue();
	    });

        Transfer transfer = setUpMinimumEvent();
        transfer.logEvent();

        assertTrue("Exception was not handled through the custom handler", exceptionHandled.isTrue());

        config.removeAppender(failingAppenderName);
    }

    @Test(expected = ConstraintValidationException.class)
    public void testAuditLogWithMissingRequestContextAttribute() {
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
        LogEventFactory.logEvent(Transfer.class, properties);
    }

    @Test(expected = ConstraintValidationException.class)
    public void testAuditLogWithMissingEventAttribute() {
        ThreadContext.put("accountNumber", "12345");
        ThreadContext.put("userId", "JohnDoe");
        ThreadContext.put("ipAddress", "127.0.0.1");
        ThreadContext.put("environment", "dev");
        ThreadContext.put("product", "TestProduct");
        ThreadContext.put("timeZone", "America/Phoenix");
        ThreadContext.put("loginId", "TestUser");
        Map<String, String> properties = new HashMap<>();
        properties.put("toAccount", "123456");
        properties.put("fromAccount", "111111");
        LogEventFactory.logEvent(Transfer.class, properties);
    }

    @Test
    public void testAuditLog() {
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
            LogEventFactory.logEvent(Transfer.class, properties);
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
}