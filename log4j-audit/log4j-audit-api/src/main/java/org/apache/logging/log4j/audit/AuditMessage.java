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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.message.StructuredDataMessage;

/**
 *
 */
public class AuditMessage extends StructuredDataMessage {

    private static final int MAX_LENGTH = 32;

    private Map<String, StructuredDataMessage> extraContent = new HashMap<>();

    public AuditMessage(String eventName) {
         this(eventName, MAX_LENGTH);
    }

    public AuditMessage(String eventName, int maxLength) {
        // Use this with Log4j 2.9
     //   super(new AuditId(eventName, MAX_LENGTH), null, "Audit", maxLength);
        super(new AuditId(eventName, maxLength), null, "Audit");
    }

    public void addContent(String name, StructuredDataMessage message) {
        extraContent.put(name, message);
    }

    private static class AuditId extends StructuredDataId {

        AuditId(String eventName, int maxLength) {
            // Use this with Log4j 2.9
            // super(eventName, maxLength);
            super(eventName, null, null);
        }

    }

}
