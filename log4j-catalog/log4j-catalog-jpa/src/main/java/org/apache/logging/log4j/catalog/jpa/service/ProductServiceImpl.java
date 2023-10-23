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

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.catalog.jpa.dao.ProductRepository;
import org.apache.logging.log4j.catalog.jpa.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Repository
@Transactional
public class ProductServiceImpl extends AbstractPagingAndSortingService implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductModel> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductModel> getProducts(String catalogId) {
        return productRepository.findByCatalogId(catalogId);
    }

    @Override
    public List<ProductModel> getProducts(int startPage, int itemsPerPage, String sortColumn, String direction) {
        Pageable pageable = createPageRequest(startPage, itemsPerPage, sortColumn, direction);
        Page<ProductModel> page = productRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public List<ProductModel> getProducts(String catalogId, int startPage, int itemsPerPage, String sortColumn,
                                          String direction) {
        Pageable pageable = createPageRequest(startPage, itemsPerPage, sortColumn, direction);
        Page<ProductModel> page = productRepository.findByCatalogId(catalogId, pageable);
        return page.getContent();
    }

    @Override
    public Optional<ProductModel> getProduct(Long productId) {
        return productRepository.findOne(productId);
    }


    @Override
    public Optional<ProductModel> getProduct(String catalogId, String name) {
        return productRepository.findByCatalogIdAndName(catalogId, name);
    }

    @Override
    public ProductModel saveProduct(ProductModel product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
