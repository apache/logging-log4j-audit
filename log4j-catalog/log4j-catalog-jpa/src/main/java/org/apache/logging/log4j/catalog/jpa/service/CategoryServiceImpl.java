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

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.jpa.dao.CategoryRepository;
import org.apache.logging.log4j.catalog.jpa.model.CategoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Repository
@Transactional
public class CategoryServiceImpl extends AbstractPagingAndSortingService implements CategoryService {
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryModel> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<CategoryModel> getCategories(String catalogId) {
        return categoryRepository.findByCatalogId(catalogId);
    }

    @Override
    public List<CategoryModel> getCategories(int startPage, int itemsPerPage, String sortColumn, String direction) {
        Pageable pageable = createPageRequest(startPage, itemsPerPage, sortColumn, direction);
        Page<CategoryModel> page = categoryRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public List<CategoryModel> getCategories(String catalogId, int startPage, int itemsPerPage, String sortColumn,
                                             String direction) {
        Pageable pageable = createPageRequest(startPage, itemsPerPage, sortColumn, direction);
        Page<CategoryModel> page = categoryRepository.findByCatalogId(catalogId, pageable);
        return page.getContent();
    }

    @Override
    public Optional<CategoryModel> getCategory(Long categoryId) {
        return categoryRepository.findOne(categoryId);
    }

    @Override
    public Optional<CategoryModel> getCategory(String catalogId, String name) {
        return categoryRepository.findByCatalogIdAndName(catalogId, name);
    }

    @Override
    public CategoryModel saveCategory(CategoryModel category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
