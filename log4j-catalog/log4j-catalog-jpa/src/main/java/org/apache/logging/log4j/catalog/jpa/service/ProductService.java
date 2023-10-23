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

import org.apache.logging.log4j.catalog.jpa.model.ProductModel;

public interface ProductService {
    List<ProductModel> getProducts(int startPage, int itemsPerPage, String sortColumn, String direction);
    List<ProductModel> getProducts(String catalogId, int startPage, int itemsPerPage, String sortColumn,
                                   String direction);
    List<ProductModel> getProducts();
    List<ProductModel> getProducts(String catalogId);
    Optional<ProductModel> getProduct(Long productId);
    Optional<ProductModel> getProduct(String catalogId, String name);
    ProductModel saveProduct(ProductModel product);
    void deleteProduct(Long productId);
}
