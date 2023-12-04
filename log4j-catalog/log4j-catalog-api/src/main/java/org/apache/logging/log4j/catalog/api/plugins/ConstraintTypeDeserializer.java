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
package org.apache.logging.log4j.catalog.api.plugins;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.logging.log4j.catalog.api.ConstraintType;
import org.apache.logging.log4j.catalog.api.exception.NameNotFoundException;

/**
 *
 */
public class ConstraintTypeDeserializer extends StdDeserializer<ConstraintType> {

    final ConstraintPlugins plugins = ConstraintPlugins.getInstance();

    public ConstraintTypeDeserializer() {
        this(null);
    }

    public ConstraintTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ConstraintType deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        String name = node.get("name").textValue();
        ConstraintType type = plugins.findByName(name);
        if (type == null) {
            throw new NameNotFoundException("Unable to locate plugin for constraint type " + name);
        }
        return type;
    }
}
