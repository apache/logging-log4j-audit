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
package org.apache.logging.log4j.audit.request;


public class RequestContextMapping {
    private final String fieldName;
    private final Scope scope;
    private final String chainKey;

    public RequestContextMapping(String fieldName, Scope scope, String chainKey) {
        this.fieldName = fieldName;
        this.scope = scope;
        this.chainKey = chainKey;
    }

    public String getChainKey() {
        return chainKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isLocal() {
        return scope == Scope.LOCAL_ONLY;
    }

    public boolean isChained() {
        return scope == Scope.CHAIN;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return fieldName + ":" + scope + ":" + chainKey;
    }
}
