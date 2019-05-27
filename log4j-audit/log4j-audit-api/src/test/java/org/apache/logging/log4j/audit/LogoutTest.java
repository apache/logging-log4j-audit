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

import org.apache.logging.log4j.audit.event.Logout;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LogoutTest extends BaseEventTest {
    @Test
    public void testAuditClassToString() {
        Logout event = LogEventFactory.getEvent(Logout.class);
        event.setCompletionStatus("Success");
        String string = event.toString();

        assertEquals("[Logout completionStatus=\"Success\"]", string);
    }

    @Test
    public void testAuditClass() {
        Logout event = LogEventFactory.getEvent(Logout.class);

        event.logEvent();

        event.setCompletionStatus("Success");

        event.logEvent();

        AuditExceptionHandler exceptionHandler = (message, ex) -> {

        };
        event.setAuditExceptionHandler(exceptionHandler);
        event.logEvent();

        List<String> msgs = app.getMessages();
        assertNotNull("No messages", msgs);
        assertEquals("No messages", 3, msgs.size());

        String msg = msgs.get(1);
        assertTrue("No completionStatus", msg.contains("completionStatus=\"Success\""));

        msg = msgs.get(2);
        assertFalse("auditExceptionHandler should not be present in the context", msg.contains("auditExceptionHandler=\""));
        msgs.forEach(System.out::println);
    }

    @Test
    public void testAuditLog() {
        LogEventFactory.logEvent(Logout.class, null);

        LogEventFactory.logEvent(Logout.class, Collections.emptyMap());
    }
}