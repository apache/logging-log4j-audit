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

import org.apache.logging.log4j.message.StructuredDataMessage;

/**
 * Handles any exceptions that may occur while logging the audit event.
 */
public interface AuditExceptionHandler {

    /**
     * Handles Exceptions that occur while audit logging. If a RuntimeException is thrown it will percolate
     * back to the application.
     * @param message The message being loggeed.
     * @param ex The Throwable.
     */
    void handleException(StructuredDataMessage message, Throwable ex);
}
