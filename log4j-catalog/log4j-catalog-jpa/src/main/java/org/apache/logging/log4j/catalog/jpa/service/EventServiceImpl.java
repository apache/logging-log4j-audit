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
package org.apache.logging.log4j.catalog.jpa.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.jpa.dao.EventRepository;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Repository
@Transactional
public class EventServiceImpl extends AbstractPagingAndSortingService implements EventService {
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<EventModel> getEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<EventModel> getEvents(String catalogId) {
        return eventRepository.findByCatalogId(catalogId);
    }

    @Override
    public List<EventModel> getEvents(int startPage, int itemsPerPage, String sortColumn, String direction) {
        Pageable pageable = createPageRequest(startPage, itemsPerPage, sortColumn, direction);
        Page<EventModel> page = eventRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public List<EventModel> getEvents(String catalogId, int startPage, int itemsPerPage, String sortColumn,
                                      String direction) {
        Pageable pageable = createPageRequest(startPage, itemsPerPage, sortColumn, direction);
        Page<EventModel> page = eventRepository.findByCatalogId(catalogId, pageable);
        return page.getContent();
    }

    @Override
    public Map<String, EventModel> getEventMap() {
        List<EventModel> events = getEvents();
        Map<String, EventModel> eventMap = new HashMap<>(events.size());
        for (EventModel event : events) {
            eventMap.put(event.getName(), event);
        }
        return eventMap;
    }

    @Override
    public Optional<EventModel> getEvent(Long eventId) {
        return eventRepository.findOne(eventId);
    }

    @Override
    public Optional<EventModel> getEvent(String catalogId, String eventName) {
        return eventRepository.findByCatalogIdAndName(catalogId, eventName);
    }

    @Override
    public EventModel saveEvent(EventModel event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }
}
