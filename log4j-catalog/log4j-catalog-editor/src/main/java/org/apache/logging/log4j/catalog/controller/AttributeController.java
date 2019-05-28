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

import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.Constraint;
import org.apache.logging.log4j.catalog.api.ListResponse;
import org.apache.logging.log4j.catalog.api.plugins.ConstraintPlugins;
import org.apache.logging.log4j.catalog.jpa.converter.AttributeConverter;
import org.apache.logging.log4j.catalog.jpa.converter.AttributeModelConverter;
import org.apache.logging.log4j.catalog.jpa.model.AttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.ConstraintModel;
import org.apache.logging.log4j.catalog.jpa.service.AttributeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.Set;

/**
 * Catalog Product controller
 */

@RequestMapping(value = "/api/attributes")
@RestController
public class AttributeController {
    private static final ConstraintPlugins constraintPlugins = ConstraintPlugins.getInstance();

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private AttributeModelConverter attributeModelConverter;

    @Autowired
    private AttributeConverter attributeConverter;

    @PostConstruct
    public void init() {
        modelMapper.addConverter(attributeModelConverter);
    }

    @PostMapping(value = "/list")
    public ResponseEntity<Map<String, Object>> attributeList(@RequestParam(value="jtStartIndex", required=false) Integer startIndex,
                                                             @RequestParam(value="jtPageSize", required=false) Integer pageSize,
                                                             @RequestParam(value="jtSorting", required=false) String sorting) {
        Type listType = new TypeToken<List<Attribute>>() {}.getType();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Attribute> attributes;
            if (startIndex == null || pageSize == null) {
                attributes = modelMapper.map(attributeService.getAttributes(), listType);
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
                attributes = modelMapper.map(attributeService.getAttributes(startPage, pageSize, sortColumn, sortDirection), listType);
            }
            if (attributes == null) {
                attributes = new ArrayList<>();
            }
            response.put("Result", "OK");
            response.put("Records", attributes);
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Map<String, Object>> createAttribute(@RequestBody Attribute attribute) {
        Map<String, Object> response = new HashMap<>();
        try {
            AttributeModel model = attributeConverter.convert(attribute);
            model = attributeService.saveAttribute(model);
            Attribute result = attributeModelConverter.convert(model);
            response.put("Result", "OK");
            response.put("Records", result);
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Map<String, Object>> updateAttribute(@RequestBody Attribute attribute) {
        Map<String, Object> response = new HashMap<>();
        try {
            AttributeModel model = attributeConverter.convert(attribute);
            model = attributeService.saveAttribute(model);
            Attribute result = attributeModelConverter.convert(model);
            response.put("Result", "OK");
            response.put("Records", result);
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<Map<String, Object>> deleteAttribute(@RequestBody Attribute attribute) {
        Map<String, Object> response = new HashMap<>();
        try {
            attributeService.deleteAttribute(attribute.getId());
            response.put("Result", "OK");
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ListResponse<String>> getAttributeNames() {
        List<AttributeModel> attributes = attributeService.getAttributes();
        List<String> attributeNames;
        if (attributes != null) {
            attributeNames = new ArrayList<>(attributes.size());
            for (AttributeModel model : attributes) {
                attributeNames.add(model.getName());
            }
        } else {
            attributeNames = new ArrayList<>();
        }
        ListResponse<String> response = new ListResponse<>();
        response.setResult("OK");
        response.setData(attributeNames);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/constraints")
    public ResponseEntity<Map<String, Object>> constraintList(@RequestParam("attributeId") Long attributeId) {
        Type listType = new TypeToken<List<Constraint>>() {}.getType();
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<AttributeModel> optional = attributeService.getAttribute(attributeId);
            List<Constraint> constraints = new ArrayList<>();
            if (optional.isPresent()) {
                Set<ConstraintModel> constraintModels = optional.get().getConstraints();
                if (constraintModels != null) {
                    for (ConstraintModel constraintModel : constraintModels) {
                        Constraint constraint = new Constraint();
                        constraint.setConstraintType(constraintPlugins.findByName(constraintModel.getConstraintType()));
                        constraint.setValue(constraintModel.getValue());
                        constraints.add(constraint);
                    }
                }
                response.put("Result", "OK");
                response.put("Records", constraints);
            }
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
