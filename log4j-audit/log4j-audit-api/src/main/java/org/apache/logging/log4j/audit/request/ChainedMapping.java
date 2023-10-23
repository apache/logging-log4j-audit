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
package org.apache.logging.log4j.audit.request;

import java.util.function.Supplier;

/**
 * Represents the mapping of a RequestContextMapping variable that is propagated from the client to the server with
 * as a new variable name.
 */
public class ChainedMapping extends RequestContextMapping {
    Supplier<String> supplier;

    public ChainedMapping(String fieldName, String chainedFieldName, Supplier<String> localValueSupplier) {
        super(fieldName, Scope.CHAIN, chainedFieldName);
        this.supplier = localValueSupplier;
    }

    public Supplier<String> getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier<String> supplier) {
        this.supplier = supplier;
    }
}
