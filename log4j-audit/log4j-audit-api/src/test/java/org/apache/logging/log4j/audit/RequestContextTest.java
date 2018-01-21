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

import org.apache.logging.log4j.audit.request.RequestContextMapping;
import org.apache.logging.log4j.audit.request.RequestContextMappings;
import org.apache.logging.log4j.audit.request.Scope;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestContextTest {

    @Test
    public void testRequestContext() throws Exception {
        RequestContext.getRequestId();
        RequestContext.setSessionId(UuidUtil.getTimeBasedUuid().toString());
        RequestContext.setLoginId("testuser");
        assertEquals("Incorrect loginId", "testuser", RequestContext.getLoginId());
        RequestContext.setHostName("myhost");
        assertEquals("Incorrect host name", "myhost", RequestContext.getHostName());
        RequestContext.setIpAddress("127.0.0.1");
        assertEquals("Incorrect LoginId", "127.0.0.1", RequestContext.getIpAddress());
    }

    @Test
    public void testMappings() throws Exception {
        RequestContextMappings mappings = new RequestContextMappings(RequestContext.class);
        assertEquals("Incorrect header prefix", "mycorp-context-", mappings.getHeaderPrefix());
        RequestContextMapping mapping = mappings.getMapping("hostName");
        assertNotNull("No mappings for hostName", mapping);
        assertEquals("Incorrect chain key", "callingHost", mapping.getChainKey());
        mapping = mappings.getMapping("loginId");
        assertEquals("Incorrect scope for loginId", Scope.CLIENT_SERVER, mapping.getScope());
    }
}
