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
package org.apache.logging.log4j.audit.event;

import org.apache.logging.log4j.audit.AuditEvent;
import org.apache.logging.log4j.audit.annotation.Constraint;
import org.apache.logging.log4j.audit.annotation.MaxLength;
import org.apache.logging.log4j.audit.annotation.RequestContext;
import org.apache.logging.log4j.audit.annotation.Required;

import java.math.BigDecimal;

/**
 * Transfer between accounts
 * @author generated
 */
@MaxLength(32)
@RequestContext(key="hostName")
@RequestContext(key="loginId", required=true)
@RequestContext(key="ipAddress", constraints={@Constraint(constraintType="pattern", constraintValue="^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")})
@RequestContext(key="accountNumber", required=true)
@RequestContext(key="userId", required=true)
public interface Transfer extends AuditEvent {

    /**
     * Amount : Amount to transfer
     * @param amount Amount to transfer
     */
    @Required
    void setAmount(BigDecimal amount);

    /**
     * From Account Number : Source of funds
     * @param fromAccount Source of funds
     */
    @Required
    void setFromAccount(int fromAccount);

    /**
     * To Account Number : Destination account
     * @param toAccount Destination account
     */
    @Required
    @Constraint(constraintType="minValue", constraintValue="1")
    void setToAccount(int toAccount);

}