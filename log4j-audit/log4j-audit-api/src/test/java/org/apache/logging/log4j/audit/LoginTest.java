package org.apache.logging.log4j.audit;

import org.apache.logging.log4j.audit.event.Login;
import org.junit.Test;

public class LoginTest extends BaseEventTest {
    @Test
    public void testAuditClass() {
        Login event = LogEventFactory.getEvent(Login.class);

        event.logEvent();
    }
}