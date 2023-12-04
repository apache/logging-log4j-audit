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

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.catalog.CatalogManager;
import org.apache.logging.log4j.audit.exception.AuditException;
import org.apache.logging.log4j.audit.exception.ConstraintValidationException;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.Constraint;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.EventAttribute;
import org.apache.logging.log4j.catalog.api.plugins.ConstraintPlugins;
import org.apache.logging.log4j.message.StructuredDataMessage;

/**
 * This class is used to log events generated remotely.
 */
public abstract class AbstractEventLogger {

    private static final int DEFAULT_MAX_LENGTH = 32;

    private static final ConstraintPlugins constraintPlugins = ConstraintPlugins.getInstance();

    public CatalogManager catalogManager;

    private static final AuditExceptionHandler DEFAULT_EXCEPTION_HANDLER = (message, ex) -> {
        throw new AuditException("Error logging event " + message.getId().getName(), ex);
    };

    private static final AuditExceptionHandler NOOP_EXCEPTION_HANDLER = (message, ex) -> {};

    private AuditExceptionHandler defaultAuditExceptionHandler = DEFAULT_EXCEPTION_HANDLER;

    private final int maxLength;

    protected AbstractEventLogger() {
        maxLength = DEFAULT_MAX_LENGTH;
    }

    protected AbstractEventLogger(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setCatalogManager(CatalogManager catalogManager) {
        this.catalogManager = catalogManager;
    }

    public List<String> getAttributeNames(String eventId) {
        return catalogManager.getAttributeNames(eventId);
    }

    public void setDefaultAuditExceptionHandler(AuditExceptionHandler auditExceptionHandler) {
        defaultAuditExceptionHandler = auditExceptionHandler == null ? NOOP_EXCEPTION_HANDLER : auditExceptionHandler;
    }

    public void logEvent(String eventName, Map<String, String> attributes) {
        logEvent(eventName, null, attributes, defaultAuditExceptionHandler);
    }

    public void logEvent(String eventName, String catalogId, Map<String, String> attributes) {
        logEvent(eventName, catalogId, attributes, defaultAuditExceptionHandler);
    }

    public void logEvent(String eventName, Map<String, String> attributes, AuditExceptionHandler exceptionHandler) {
        logEvent(eventName, null, attributes, exceptionHandler);
    }

    private void logEvent(
            String eventName,
            String catalogId,
            Map<String, String> attributes,
            AuditExceptionHandler exceptionHandler) {
        Event event =
                catalogId == null ? catalogManager.getEvent(eventName) : catalogManager.getEvent(eventName, catalogId);
        if (event == null) {
            throw new AuditException("Unable to locate definition of audit event " + eventName);
        }
        logEvent(eventName, attributes, event, exceptionHandler);
    }

    protected abstract void logEvent(StructuredDataMessage message);

    private void logEvent(
            String eventName, Map<String, String> attributes, Event event, AuditExceptionHandler exceptionHandler) {
        AuditMessage msg = new AuditMessage(eventName, maxLength);

        if (attributes == null) {
            attributes = emptyMap();
        }

        StringBuilder missingAttributes = new StringBuilder();
        StringBuilder errors = new StringBuilder();

        List<EventAttribute> eventAttributes = event.getAttributes() == null ? emptyList() : event.getAttributes();
        for (EventAttribute eventAttribute : eventAttributes) {
            Attribute attr = catalogManager.getAttribute(eventAttribute.getName(), event.getCatalogId());
            if ((!attr.isRequestContext() && (attr.isRequired())
                    || (eventAttribute.isRequired() != null && eventAttribute.isRequired()))) {
                String name = attr.getName();
                if (!attributes.containsKey(name)) {
                    if (missingAttributes.length() > 0) {
                        missingAttributes.append(", ");
                    }
                    missingAttributes.append(name);
                } else {
                    if (attr.getConstraints() != null && attr.getConstraints().size() > 0) {
                        validateConstraints(false, attr.getConstraints(), name, attributes.get(name), errors);
                    }
                }
            }
        }
        Map<String, Attribute> attributeMap = catalogManager.getAttributes(eventName, event.getCatalogId());
        for (String name : attributes.keySet()) {
            if (!attributeMap.containsKey(name) && !name.equals("completionStatus")) {
                if (errors.length() > 0) {
                    errors.append("\n");
                }
                errors.append("Attribute ")
                        .append(name)
                        .append(" is not defined for ")
                        .append(eventName);
            }
        }
        if (missingAttributes.length() > 0) {
            if (errors.length() > 0) {
                errors.append("\n");
            }
            errors.append("Event ")
                    .append(eventName)
                    .append(" is missing required attribute(s) ")
                    .append(missingAttributes.toString());
        }
        if (errors.length() > 0) {
            throw new ConstraintValidationException(errors.toString());
        }
        List<String> attributeNames = catalogManager.getAttributeNames(eventName, event.getCatalogId());
        StringBuilder buf = new StringBuilder();
        for (String attribute : attributes.keySet()) {
            if (!attributeNames.contains(attribute)) {
                if (buf.length() > 0) {
                    buf.append(", ");
                }
                buf.append(attribute);
            }
        }
        if (buf.length() > 0) {
            throw new ConstraintValidationException(
                    "Event " + eventName + " contains invalid attribute(s) " + buf.toString());
        }

        List<String> reqCtxAttrs = catalogManager.getRequiredContextAttributes(eventName, event.getCatalogId());
        if (reqCtxAttrs != null && !reqCtxAttrs.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String attr : reqCtxAttrs) {
                if (!ThreadContext.containsKey(attr)) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(attr);
                }
            }
            if (sb.length() > 0) {
                throw new ConstraintValidationException("Event " + msg.getId().getName()
                        + " is missing required RequestContextMapping values for " + sb.toString());
            }
        }

        Map<String, Attribute> reqCtxAttributes = catalogManager.getRequestContextAttributes();
        for (Map.Entry<String, Attribute> entry : reqCtxAttributes.entrySet()) {
            Attribute attribute = entry.getValue();
            String attr = entry.getKey();
            if (attribute.isRequired() && !ThreadContext.containsKey(attr)) {
                if (errors.length() > 0) {
                    errors.append(", ");
                }
                errors.append(attr);
            }
        }
        if (errors.length() > 0) {
            throw new ConstraintValidationException(
                    "Event " + eventName + " is missing required Thread Context values for " + errors.toString());
        }

        for (Map.Entry<String, Attribute> entry : reqCtxAttributes.entrySet()) {
            Attribute attribute = reqCtxAttributes.get(entry.getKey());
            if (!ThreadContext.containsKey(entry.getKey())) {
                continue;
            }
            Set<Constraint> constraintList = attribute.getConstraints();
            if (constraintList != null && constraintList.size() > 0) {
                validateConstraints(true, constraintList, entry.getKey(), ThreadContext.get(entry.getKey()), errors);
            }
        }
        if (errors.length() > 0) {
            throw new ConstraintValidationException(
                    "Event " + eventName + " has incorrect data in the Thread Context: " + errors.toString());
        }

        msg.putAll(attributes);
        try {
            logEvent(msg);
        } catch (Throwable ex) {
            if (exceptionHandler == null) {
                defaultAuditExceptionHandler.handleException(msg, ex);
            } else {
                exceptionHandler.handleException(msg, ex);
            }
        }
    }

    private static void validateConstraints(
            boolean isRequestContext,
            Collection<Constraint> constraints,
            String name,
            String value,
            StringBuilder errors) {
        for (Constraint constraint : constraints) {
            constraintPlugins.validateConstraint(
                    isRequestContext,
                    constraint.getConstraintType().getName(),
                    name,
                    value,
                    constraint.getValue(),
                    errors);
        }
    }
}
