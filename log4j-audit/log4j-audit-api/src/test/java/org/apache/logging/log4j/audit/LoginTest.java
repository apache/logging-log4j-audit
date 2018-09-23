package org.apache.logging.log4j.audit;

import org.apache.logging.log4j.audit.event.Login;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LoginTest extends BaseEventTest {
    @Test
    public void testAuditClass() {
        Login event = LogEventFactory.getEvent(Login.class);

        event.logEvent();

        event.setCompletionStatus("Success");

        event.logEvent();

        AuditExceptionHandler exceptionHandler = (message, ex) -> {

        };
        event.setAuditExceptionHandler(exceptionHandler);
        event.logEvent();

        List<String> msgs = app.getMessages();
        assertNotNull("No messages", msgs);
        assertTrue("No messages", msgs.size() == 3);

        String msg = msgs.get(1);
        assertTrue("No completionStatus", msg.contains("completionStatus=\"Success\""));

        msg = msgs.get(2);
        assertTrue("auditExceptionHandler should not be present in the context", !msg.contains("auditExceptionHandler=\""));
        msgs.forEach(System.out::println);
    }

    @Test
    public void testAuditLog() {
        LogEventFactory.logEvent(Login.class, null);

        LogEventFactory.logEvent(Login.class, Collections.emptyMap());
    }
}