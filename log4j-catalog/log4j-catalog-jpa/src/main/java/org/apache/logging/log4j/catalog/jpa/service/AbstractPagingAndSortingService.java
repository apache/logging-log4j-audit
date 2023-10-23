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
package org.apache.logging.log4j.catalog.jpa.service;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class AbstractPagingAndSortingService {

    protected Pageable createPageRequest(int startPage, int itemsPerPage, String sortColumn, String direction) {
        PageRequest pageRequest;
        if (sortColumn == null || sortColumn.length() == 0) {
            pageRequest = PageRequest.of(startPage, itemsPerPage);
        } else {
            Sort.Direction sortDirection;
            if (direction == null) {
                sortDirection = Sort.Direction.ASC;
            } else {
                sortDirection = Sort.Direction.fromOptionalString(direction).orElse(Direction.ASC);
            }
            pageRequest = PageRequest.of(startPage, itemsPerPage, Sort.by(sortDirection, sortColumn));
        }
        return pageRequest;
    }
}
