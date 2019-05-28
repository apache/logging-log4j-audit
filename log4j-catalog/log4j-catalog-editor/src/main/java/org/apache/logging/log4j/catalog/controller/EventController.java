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
package org.apache.logging.log4j.catalog.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.jpa.converter.EventConverter;
import org.apache.logging.log4j.catalog.jpa.converter.EventModelConverter;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Catalog Product controller
 */

@RequestMapping(value = "/api/events")
@RestController
public class EventController {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private EventService eventService;

    @Autowired
    private EventModelConverter eventModelConverter;

    @Autowired
    private EventConverter eventConverter;

    @PostConstruct
    public void init() {
        modelMapper.addConverter(eventModelConverter);
    }

    @PostMapping(value = "/list")
    public ResponseEntity<Map<String, Object>> eventList(@RequestParam(value="jtStartIndex", required=false) Integer startIndex,
                                                         @RequestParam(value="jtPageSize", required=false) Integer pageSize,
                                                         @RequestParam(value="jtSorting", required=false) String sorting) {
        Type listType = new TypeToken<List<Event>>() {}.getType();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Event> events;
            if (startIndex == null || pageSize == null) {
                events = modelMapper.map(eventService.getEvents(), listType);
            } else {
                int startPage = 0;
                if (startIndex > 0) {
                    startPage = startIndex / pageSize;
                }
                String sortColumn = "name";
                String sortDirection = "ASC";
                if (sorting != null) {
                    String[] sortInfo = sorting.split(" ");
                    sortColumn = sortInfo[0];
                    if (sortInfo.length > 1) {
                        sortDirection = sortInfo[1];
                    }
                }
                events = modelMapper.map(eventService.getEvents(startPage, pageSize, sortColumn, sortDirection), listType);
            }
            if (events == null) {
                events = new ArrayList<>();
            }
            response.put("Result", "OK");
            response.put("Records", events);
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
            response.put("Message", ex.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody Event event) {
        Map<String, Object> response = new HashMap<>();
        try {
            EventModel model = eventConverter.convert(event);
            event = eventModelConverter.convert(eventService.saveEvent(model));
            response.put("Result", "OK");
            response.put("Records", event);
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
            response.put("Message", ex.getMessage());
            LOGGER.warn("Unable to create event named {}", event.getName(), ex);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Map<String, Object>> updateEvent(@RequestBody Event event) {
        Map<String, Object> response = new HashMap<>();
        try {
            EventModel model = eventConverter.convert(event);
            event = eventModelConverter.convert(eventService.saveEvent(model));
            response.put("Result", "OK");
            response.put("Records", event);
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
            response.put("Message", ex.getMessage());
            LOGGER.warn("Unable to update event named {}", event.getName(), ex);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<Map<String, Object>> deleteEvent(@RequestBody Long eventId) {
        Map<String, Object> response = new HashMap<>();
        try {
            eventService.deleteEvent(eventId);
            response.put("Result", "OK");
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
            response.put("Message", ex.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/attributes/list")
    public ResponseEntity<Map<String, Object>> attributeList(@RequestParam("eventId") Long eventId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<EventModel> optional = eventService.getEvent(eventId);
            if (optional.isPresent()) {
                Event event = eventModelConverter.convert(optional.get());
                response.put("Result", "OK");
                if (event != null && event.getAttributes() != null) {
                    response.put("Records", event.getAttributes());
                } else {
                    response.put("Records", new ArrayList<>());
                }
            } else {
                response.put("Result", "OK");
                response.put("Records", new ArrayList<>());

            }
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
