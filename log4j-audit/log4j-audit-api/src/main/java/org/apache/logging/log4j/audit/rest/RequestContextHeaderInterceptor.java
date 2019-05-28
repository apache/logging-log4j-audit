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
package org.apache.logging.log4j.audit.rest;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.request.RequestContextMapping;
import org.apache.logging.log4j.audit.request.RequestContextMappings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Creates a List of Headers containing the keys and values in the RequestContext that have a mapping indicating
 * they should be propogated to the service being called.
 *
 * This class is designed to be used by Spring as part of the REST Template configuration when calling a REST service.
 *
 */
public class RequestContextHeaderInterceptor implements ClientHttpRequestInterceptor {

    private RequestContextMappings mappings;

    public RequestContextHeaderInterceptor(RequestContextMappings mappings) {
        this.mappings = mappings;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        Map<String, String> map = ThreadContext.getImmutableContext();
        HttpHeaders headers = httpRequest.getHeaders();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            RequestContextMapping mapping = mappings.getMapping(entry.getKey());
            if (mapping != null && !mapping.isLocal()) {
                String key = mappings.getHeaderPrefix() + mapping.getFieldName();
                if (!headers.containsKey(key)) {
                    headers.add(key, entry.getValue());
                }
            }
        }
        return clientHttpRequestExecution.execute(httpRequest, body);
    }
}
