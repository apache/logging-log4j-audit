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
package org.apache.logging.log4j.audit.service.controller;

import java.util.Map;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.AuditLogger;
import org.apache.logging.log4j.audit.service.Versions;
import org.apache.logging.log4j.audit.dto.AuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
public class AuditController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private AuditLogger auditLogger;

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Generate an Audit event", notes = "Causes an Audit event to be logged", tags = {"Audit"})
    @PostMapping(value = "/event/log", produces = Versions.V1_0_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public void logEvent(@RequestBody AuditDto auditDto) {
        ThreadContext.clearMap();
        try {
            for (Map.Entry<String, String> entry : auditDto.getRequestContextMap().entrySet()) {
                ThreadContext.put(entry.getKey(), entry.getValue());
            }
            auditLogger.logEvent(auditDto.getEventName(), auditDto.getProperties());
        } finally {
            ThreadContext.clearMap();
        }
    }
}