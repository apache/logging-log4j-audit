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
import java.text.DecimalFormat;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.request.ChainedMapping;
import org.apache.logging.log4j.audit.request.RequestContextMapping;
import org.apache.logging.log4j.audit.request.RequestContextMappings;

/**
 * Filter to initialize and clear the RequestContext.
 */
public class RequestContextFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(RequestContextFilter.class);
    private final Class<?> requestContextClass;
    private RequestContextMappings mappings;

    public RequestContextFilter() {
        requestContextClass = null;
    }

    public RequestContextFilter(Class<?> clazz) {
        requestContextClass = clazz;
    }

    /**
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (requestContextClass != null) {
            mappings = new RequestContextMappings(requestContextClass);
        } else {
            String requestContextClassName = filterConfig.getInitParameter("requestContextClass");
            if (requestContextClassName == null) {
                logger.error("No RequestContext class name was provided");
                throw new IllegalArgumentException("No RequestContext class name provided");
            }
            mappings = new RequestContextMappings(requestContextClassName);
        }
    }

    /**
     * Manages the initialization and clearing of the RequestContext.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            logger.trace("Starting request {}", request.getRequestURI());
            try {
                Enumeration<String> headers = request.getHeaderNames();
                while (headers.hasMoreElements()) {
                    String name = headers.nextElement();
                    RequestContextMapping mapping = mappings.getMappingByHeader(name);
                    logger.debug("Got Mapping:{} for Header:{}", mapping, name);
                    if (mapping != null) {
                        if (mapping.isChained()) {
                            ThreadContext.put(mapping.getChainKey(), request.getHeader(name));
                            logger.debug("Setting Context Key:{} with value:{}", mapping.getChainKey(), request.getHeader(name));
                            String value = ((ChainedMapping)mapping).getSupplier().get();
                            ThreadContext.put(mapping.getFieldName(), value);
                            logger.debug("Setting Context Key:{} with value:{}", mapping.getFieldName(), value);
                        } else {
                            ThreadContext.put(mapping.getFieldName(), request.getHeader(name));
                            logger.debug("Setting Context Key:{} with value:{}", mapping.getFieldName(), request.getHeader(name));
                        }
                    }
                }
                long start = 0;
                if (logger.isTraceEnabled()) {
                    start = System.nanoTime();
                }
                filterChain.doFilter(servletRequest, servletResponse);
                if (logger.isTraceEnabled()) {
                    long elapsed = System.nanoTime() - start;
                    StringBuilder sb = new StringBuilder("Request ").append(request.getRequestURI()).append(" completed in ");
                    ElapsedUtil.addElapsed(elapsed, sb);
                    logger.trace(sb.toString());
                }
            } catch (Throwable e) {
                logger.error("Application cascaded error", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                ThreadContext.clearMap();
            }
        }
    }

    public void destroy() {
    }
}
