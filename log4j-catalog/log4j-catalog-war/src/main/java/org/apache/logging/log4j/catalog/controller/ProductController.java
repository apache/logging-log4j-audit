package org.apache.logging.log4j.catalog.controller;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.Product;
import org.apache.logging.log4j.catalog.jpa.model.ProductModel;
import org.apache.logging.log4j.catalog.jpa.service.ProductService;
import org.apache.logging.log4j.catalog.jpa.converter.ProductConverter;
import org.apache.logging.log4j.catalog.jpa.converter.ProductModelConverter;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Catalog Product controller
 */

@RequestMapping(value = "/api/products")
@RestController
public class ProductController {
    private static final Logger LOGGER = LogManager.getLogger();

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductModelConverter productModelConverter;

    @Autowired
    private ProductConverter productConverter;

    @PostConstruct
    public void init() {
        modelMapper.addConverter(productModelConverter);
    }

    @PostMapping(value = "/list")
    public ResponseEntity<Map<String, Object>> productList(@RequestParam(value="jtStartIndex", required=false) Integer startIndex,
                                                           @RequestParam(value="jtPageSize", required=false) Integer pageSize,
                                                           @RequestParam(value="jtSorting", required=false) String sorting) {
        Type listType = new TypeToken<List<Product>>() {}.getType();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Product> products = null;
            if (startIndex == null || pageSize == null) {
                products = modelMapper.map(productService.getProducts(), listType);
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
                    if (sortInfo.length > 0) {
                        sortDirection = sortInfo[1];
                    }
                }
                products = modelMapper.map(productService.getProducts(startPage, pageSize, sortColumn, sortDirection), listType);
            }
            if (products == null) {
                products = new ArrayList<>();
            }
            response.put("Result", "OK");
            response.put("Records", products);
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductModel model = productConverter.convert(product);
            model = productService.saveProduct(model);
            response.put("Result", "OK");
            response.put("Records", productModelConverter.convert(model));
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductModel model = productConverter.convert(product);
            model = productService.saveProduct(model);
            response.put("Result", "OK");
            response.put("Records", productModelConverter.convert(model));
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<Map<String, Object>> deleteProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.deleteProduct(product.getId());
            response.put("Result", "OK");
        } catch (Exception ex) {
            response.put("Result", "FAILURE");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
