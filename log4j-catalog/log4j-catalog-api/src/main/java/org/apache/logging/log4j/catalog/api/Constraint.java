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
package org.apache.logging.log4j.catalog.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 */
public class Constraint implements Serializable {

    private static final long serialVersionUID = -6880181600556259104L;

    @JsonIgnore
    private Long id;

    /**
     * The type of constraint to be applied.
     */
    private ConstraintType constraintType;

    /**
     * The data value to be used to apply this constraint.
     */
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public Constraint setConstraintType(ConstraintType constraintType) {
        this.constraintType = constraintType;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Constraint setValue(String value) {
        this.value = value;
        return this;
    }
}
