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

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.annotation.Chained;
import org.apache.logging.log4j.audit.annotation.ChainedSupplier;
import org.apache.logging.log4j.audit.annotation.ClientServer;
import org.apache.logging.log4j.audit.annotation.HeaderPrefix;
import org.apache.logging.log4j.audit.annotation.Local;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.UuidUtil;

/**
 * Defines all the variables that an application needs to be available in the ThreadContext for audit logging and
 * general application usage.
 */
@HeaderPrefix("mycorp-context-")
public final class RequestContext {

    @ClientServer
    public static final String REQUEST_ID = "requestId";
    @ClientServer
    public static final String SESSION_ID = "sessionId";
    @ClientServer
    public static final String ACCOUNT_NUMBER = "accountNumber";
    @ClientServer
    public static final String IP_ADDRESS = "ipAddress";
    @ClientServer
    public static final String USER_ID = "userId";
    @ClientServer
    public static final String LOGIN_ID = "loginId";
    @Local
    public static final String CALLING_HOST = "callingHost";

    public static final String HOST_NAME = "hostName";

    private static final String LOCAL_HOST_NAME = NetUtils.getLocalHostname();
    /**
     * The Supplier is used to populate the hostName key after the hostName value from the caller has been
     * placed into the callingHost map entry.
     */
    @Chained(fieldName = HOST_NAME, chainedFieldName = CALLING_HOST)
    public static final Supplier<String> LOCAL_HOST_SUPPLIER = () -> LOCAL_HOST_NAME;

    public static void clear() {
        ThreadContext.clearMap();
    }

    public static String getRequestId() {
        String uuidStr = ThreadContext.get(REQUEST_ID);
        UUID uuid;
        if (uuidStr == null) {
            uuid = UuidUtil.getTimeBasedUuid();
            ThreadContext.put(REQUEST_ID, uuid.toString());
        }
        return uuidStr;
    }

    public static String getSessionId() {
        return ThreadContext.get(SESSION_ID);
    }

    public static void setSessionId(UUID sessionId) {
        if (sessionId != null) {
            ThreadContext.put(SESSION_ID, sessionId.toString());
        }
    }

    public static void setSessionId(String sessionId) {
        if (sessionId != null) {
            ThreadContext.put(SESSION_ID, sessionId);
        }
    }

    public static void setAccountNumber(Long accountNumber) {
        ThreadContext.put(ACCOUNT_NUMBER, accountNumber.toString());
    }

    public static Long getAccountNumber() {
        String value = ThreadContext.get(ACCOUNT_NUMBER);
        if (value == null || value.length() == 0) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static void setIpAddress(String address) {
        ThreadContext.put(IP_ADDRESS, address);
    }

    public static String getIpAddress() {
        return ThreadContext.get(IP_ADDRESS);
    }

    public static void setUserId(String userId) {
        ThreadContext.put(USER_ID, userId);
    }

    public static String getUserId() {
        return ThreadContext.get(USER_ID);
    }

    public static void setLoginId(String loginId) {
        ThreadContext.put(LOGIN_ID, loginId);
    }

    public static String getLoginId() {
        return ThreadContext.get(LOGIN_ID);
    }

    public static String getHostName() {
        return ThreadContext.get(HOST_NAME);
    }

    public static void setHostName(String hostName) {
        ThreadContext.put(HOST_NAME, hostName);
    }

    public static String getCallingHost() {
        return ThreadContext.get(CALLING_HOST);
    }

    public static void setCallingHost(String hostName) {
        ThreadContext.put(CALLING_HOST, hostName);
    }
}
