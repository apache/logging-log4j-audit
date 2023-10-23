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

import org.apache.logging.log4j.audit.request.RequestContextMapping;
import org.apache.logging.log4j.audit.request.RequestContextMappings;
import org.apache.logging.log4j.audit.request.Scope;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestContextTest {

    @Test
    public void testRequestContext() {
        RequestContext.getRequestId();

        String sessionId = UuidUtil.getTimeBasedUuid().toString();
        RequestContext.setSessionId(sessionId);

        String loginId = "testuser";
        RequestContext.setLoginId(loginId);
        assertEquals("Incorrect loginId", loginId, RequestContext.getLoginId());

        String hostName = "myhost";
        RequestContext.setHostName(hostName);
        assertEquals("Incorrect host name", hostName, RequestContext.getHostName());

        String ipAddress = "127.0.0.1";
        RequestContext.setIpAddress(ipAddress);
        assertEquals("Incorrect LoginId", ipAddress, RequestContext.getIpAddress());

        RequestContext requestContext = RequestContext.save();
        RequestContext.clear();

        assertNull(RequestContext.getSessionId());
        assertNull(RequestContext.getLoginId());
        assertNull(RequestContext.getHostName());
        assertNull(RequestContext.getIpAddress());

        requestContext.restore();

        assertEquals(sessionId, RequestContext.getSessionId());
        assertEquals(loginId, RequestContext.getLoginId());
        assertEquals(hostName, RequestContext.getHostName());
        assertEquals(ipAddress, RequestContext.getIpAddress());
    }

    @Test
    public void testMappings() {
        RequestContextMappings mappings = new RequestContextMappings(RequestContext.class);
        assertEquals("Incorrect header prefix", "mycorp-context-", mappings.getHeaderPrefix());
        RequestContextMapping mapping = mappings.getMapping("hostName");
        assertNotNull("No mappings for hostName", mapping);
        assertEquals("Incorrect chain key", "callingHost", mapping.getChainKey());
        mapping = mappings.getMapping("loginId");
        assertEquals("Incorrect scope for loginId", Scope.CLIENT_SERVER, mapping.getScope());
    }
}
