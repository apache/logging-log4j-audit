package org.apache.logging.log4j.catalog.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.Constraint;
import org.apache.logging.log4j.catalog.jpa.model.ConstraintModel;
import org.apache.logging.log4j.catalog.jpa.service.ConstraintService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Constraint controller
 */

@RequestMapping(value = "/api/constraints")
@RestController
public class ConstraintController {
    private static final Logger LOGGER = LogManager.getLogger();

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ConstraintService constraintService;

    @PostMapping(value = "/list")
    public ResponseEntity<Map<String, Object>> attributeList(@RequestParam("attributeId") Long attributeId) {
        Type listType = new TypeToken<List<Attribute>>() {}.getType();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Attribute> attributes = modelMapper.map(constraintService.getConstraints(), listType);
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

    @GetMapping(value = "/types")
    public ResponseEntity<Set<String>> getConstraintTypes() {
        return new ResponseEntity<>(constraintService.getConstraintTypes(), HttpStatus.OK);
    }

    @PostMapping(value = "/constraint")
    public ResponseEntity<Long> addConstraint(@RequestBody Constraint constraint) {
        ConstraintModel model = modelMapper.map(constraint, ConstraintModel.class);
        model = constraintService.saveConstraint(model);
        return new ResponseEntity<>(model.getId(), HttpStatus.CREATED);
    }

    @PutMapping(value = "/constraint/{id}")
    public ResponseEntity<Long> updateConstraint(@RequestParam Long id, @RequestBody Constraint constraint) {
        ConstraintModel model = modelMapper.map(constraint, ConstraintModel.class);
        model.setId(id);
        model = constraintService.saveConstraint(model);
        return new ResponseEntity<>(model.getId(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/constraint/{id}")
    public ResponseEntity<?> deleteConstraint(@RequestParam Long id) {
        constraintService.deleteConstraint(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
