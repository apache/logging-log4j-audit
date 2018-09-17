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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.request.ChainedMapping;
import org.apache.logging.log4j.audit.request.RequestContextMapping;
import org.apache.logging.log4j.audit.request.RequestContextMappings;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * May be used instead of the RequestContextFilter to convert RequestContext headers to ThreadContext variables.
 */
public class RequestContextHandlerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(RequestContextHandlerInterceptor.class);
    private RequestContextMappings mappings;
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    public RequestContextHandlerInterceptor(Class<?> clazz) {
        mappings = new RequestContextMappings(clazz);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        logger.info("Starting request {}", request.getRequestURI());
        Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = (String) headers.nextElement();
            RequestContextMapping mapping = mappings.getMappingByHeader(name);
            logger.debug("Got Mapping:{} for Header:{}", mapping, name);
            if (mapping != null) {
                if (mapping.isChained()) {
                    ThreadContext.put(mapping.getChainKey(), request.getHeader(name));
                    logger.debug("Setting Context Key:{} with value:{}", mapping.getChainKey(), request.getHeader(name));
                    String value = ((ChainedMapping) mapping).getSupplier().get();
                    ThreadContext.put(mapping.getFieldName(), value);
                    logger.debug("Setting Context Key:{} with value:{}", mapping.getFieldName(), value);
                } else {
                    ThreadContext.put(mapping.getFieldName(), request.getHeader(name));
                    logger.debug("Setting Context Key:{} with value:{}", mapping.getFieldName(), request.getHeader(name));
                }
            }
        }
        startTime.set(System.nanoTime());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
        long elapsed = System.nanoTime() - startTime.get();
        StringBuilder sb = new StringBuilder("Request ").append(request.getRequestURI()).append(" completed in ");
        ElapsedUtil.addElapsed(elapsed, sb);
        logger.info(sb.toString());
        startTime.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        ThreadContext.clearMap();
    }
}
