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

import org.apache.logging.log4j.catalog.api.plugins.ConstraintPlugins;
import org.apache.logging.log4j.catalog.jpa.dao.ConstraintRepository;
import org.apache.logging.log4j.catalog.jpa.model.ConstraintModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Repository
@Transactional
public class ConstraintServiceImpl implements ConstraintService {

    @Autowired
    private ConstraintRepository constraintRepository;

    @Override
    public Set<String> getConstraintTypes() {
        return ConstraintPlugins.getInstance().getConstraintMap().keySet();
    }

    @Override
    public List<ConstraintModel> getConstraints() {
        return constraintRepository.findAll();
    }

    @Override
    public Optional<ConstraintModel> getConstraint(Long constraintId) {
        return constraintRepository.findOne(constraintId);
    }

    @Override
    public ConstraintModel saveConstraint(ConstraintModel constraint) {
        return constraintRepository.save(constraint);
    }

    @Override
    public void deleteConstraint(Long constraintId) {
        constraintRepository.deleteById(constraintId);
    }

}
