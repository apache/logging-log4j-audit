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
package org.apache.logging.log4j.catalog.config;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 *  Extends Jackson ObjectMapper to support Java LocalDateTime.
 */
public final class JsonObjectMapperFactory {
    /**
     * Date/Time format.
     */
    private static final String LOCAL_DATE_TIME_FORMAT = "yyyyMMddHHmmss.SSS";

    /**
     * LocalDateTime formatter that converts to and from a format usable in REST requests.
     */
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT);

    /**
     * Date/Time format.
     */
    private static final String LOCAL_DATE_FORMAT = "yyyyMMdd";

    /**
     * LocalDateTime formatter that converts to and from a format usable in REST requests.
     */
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT);

    /**
     * Date/Time format.
     */
    private static final String ZONED_DATE_TIME_FORMAT = "yyyyMMddHHmmss.SSSZ";

    /**
     * LocalDateTime formatter that converts to and from a format usable in REST requests.
     */
    public static final DateTimeFormatter ZONED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ZONED_DATE_TIME_FORMAT);

    private JsonObjectMapperFactory() {
    }

    /**
     * Create an ObjectMapper using the standard LocalDateTime format.
     * @return The ObjectMapper.
     */
    public static ObjectMapper createMapper() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        DateTimeFormatter dateTimeFormatter = LOCAL_DATE_TIME_FORMATTER;
        DateTimeFormatter dateFormatter = LOCAL_DATE_FORMATTER;
        DateTimeFormatter zonedTimeFormatter = ZONED_DATE_TIME_FORMATTER;
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
                    SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                jsonGenerator.writeString(dateTimeFormatter.format(localDateTime));
            }
        });
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                try {
                    return LocalDateTime.parse(string, dateTimeFormatter);
                } catch (DateTimeException e) {
                    throw JsonMappingException.from(parser,
                            String.format("Failed to deserialize %s: (%s) %s",
                                    handledType().getName(), e.getClass().getName(), e.getMessage()), e);
                }
            }
        });
        module.addSerializer(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
            @Override
            public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator,
                    SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                jsonGenerator.writeString(zonedTimeFormatter.format(zonedDateTime));
            }
        });
        module.addDeserializer(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
            @Override
            public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                try {
                    return ZonedDateTime.parse(string, zonedTimeFormatter);
                } catch (DateTimeException e) {
                    throw JsonMappingException.from(parser,
                            String.format("Failed to deserialize %s: (%s) %s",
                                    handledType().getName(), e.getClass().getName(), e.getMessage()), e);
                }
            }
        });
        module.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate localDate, JsonGenerator jsonGenerator,
                    SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                jsonGenerator.writeString(dateFormatter.format(localDate));
            }
        });
        module.addDeserializer(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                try {
                    return LocalDate.parse(string, dateFormatter);
                } catch (DateTimeException e) {
                    throw JsonMappingException.from(parser,
                            String.format("Failed to deserialize %s: (%s) %s",
                                    handledType().getName(), e.getClass().getName(), e.getMessage()), e);
                }
            }
        });
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper;
    }

}
