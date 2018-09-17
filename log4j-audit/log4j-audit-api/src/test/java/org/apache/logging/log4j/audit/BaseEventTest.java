package org.apache.logging.log4j.audit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class BaseEventTest {

    protected static LoggerContext ctx;
    protected static ListAppender app;

    @BeforeClass
    public static void setupClass() throws Exception {
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
        ThreadContext.clearMap();
    }
}