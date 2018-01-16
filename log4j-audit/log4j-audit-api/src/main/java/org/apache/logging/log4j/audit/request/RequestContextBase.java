
package org.apache.logging.log4j.audit.request;

import java.util.Map;

import org.apache.logging.log4j.ThreadContext;

public abstract class RequestContextBase {

    private static RequestContextMappings mappings = null;

    protected static void setMappings(RequestContextMappings requestContextMappings) {
        if (mappings != null) {
            throw new IllegalStateException("RequestContextMappings were previously set");
        }
        mappings = requestContextMappings;
    }

    public static RequestContextMappings getMappings() {
        return mappings;
    }

    public static void clear() {
        ThreadContext.clearMap();
    }

    public static String get(String key) {
        return ThreadContext.get(key);
    }

    public static Map<String, String> get() {
        return ThreadContext.getImmutableContext();
    }
}
