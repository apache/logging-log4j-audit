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
package org.apache.logging.log4j.audit.service.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.audit.AuditLogger;
import org.apache.logging.log4j.audit.dto.AuditDto;
import org.apache.logging.log4j.catalog.api.Versions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditController {

    @Autowired
    private AuditLogger auditLogger;

    @ApiImplicitParams({@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(
            value = "Generate an Audit event",
            notes = "Causes an Audit event to be logged",
            tags = {"Audit"})
    @PostMapping(value = "/event/log", produces = Versions.V1_0)
    @ResponseStatus(value = HttpStatus.OK)
    public void logEvent(@RequestBody AuditDto auditDto) {
        try {
            Map<String, String> requestContextMap = auditDto.getRequestContextMap();
            if (requestContextMap != null) {
                for (Map.Entry<String, String> entry : requestContextMap.entrySet()) {
                    ThreadContext.put(entry.getKey(), entry.getValue());
                }
            }
            auditLogger.logEvent(auditDto.getEventName(), auditDto.getCatalogId(), auditDto.getProperties());
        } finally {
            ThreadContext.clearMap();
        }
    }
}
