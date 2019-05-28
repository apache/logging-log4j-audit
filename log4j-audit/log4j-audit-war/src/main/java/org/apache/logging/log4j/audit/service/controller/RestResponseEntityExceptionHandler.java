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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.rest.webmvc.support.ExceptionMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @ExceptionHandler({ Exception.class })
    @ResponseBody
    public ResponseEntity<?> handleAnyException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } else if (e instanceof IllegalStateException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<ExceptionMessage> errorResponse(Throwable throwable,
                                                             HttpStatus status) {
        if (null != throwable) {
            LOGGER.error("error caught: " + throwable.getMessage(), throwable);
            return response(new ExceptionMessage(throwable), status);
        } else {
            LOGGER.error("unknown error caught in RESTController, {}", status);
            return response(null, status);
        }
    }

    protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
        LOGGER.debug("Responding with a status of {}", status);
        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }
}
