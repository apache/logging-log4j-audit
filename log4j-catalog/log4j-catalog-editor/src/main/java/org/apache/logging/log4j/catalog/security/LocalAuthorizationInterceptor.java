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
package org.apache.logging.log4j.catalog.security;

import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LocalAuthorizationInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String token;

    public LocalAuthorizationInterceptor(String token) {
        this.token = token;
    }

    @Override
    public boolean preHandle(
            javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpServletResponse response,
            Object handler)
            throws Exception {
        LOGGER.traceEntry();
        try {
            if (request.getServletPath().startsWith("/swagger")) {
                return true;
            }

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.equals(token)) {
                LOGGER.error("Authorization value of " + authHeader + " does not match expected value of " + token);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            return true;
        } finally {
            LOGGER.traceExit();
        }
    }
}
