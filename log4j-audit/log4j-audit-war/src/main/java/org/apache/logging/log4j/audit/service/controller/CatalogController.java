package org.apache.logging.log4j.audit.service.controller;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.Product;
import org.apache.logging.log4j.catalog.api.Versions;
import org.apache.logging.log4j.catalog.jpa.converter.AttributeConverter;
import org.apache.logging.log4j.catalog.jpa.converter.AttributeModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.CategoryConverter;
import org.apache.logging.log4j.catalog.jpa.converter.CategoryModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.EventConverter;
import org.apache.logging.log4j.catalog.jpa.converter.EventModelConverter;
import org.apache.logging.log4j.catalog.jpa.converter.ProductConverter;
import org.apache.logging.log4j.catalog.jpa.converter.ProductModelConverter;
import org.apache.logging.log4j.catalog.jpa.model.AttributeModel;
import org.apache.logging.log4j.catalog.jpa.model.CategoryModel;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;
import org.apache.logging.log4j.catalog.jpa.model.ProductModel;
import org.apache.logging.log4j.catalog.jpa.service.AttributeService;
import org.apache.logging.log4j.catalog.jpa.service.CategoryService;
import org.apache.logging.log4j.catalog.jpa.service.EventService;
import org.apache.logging.log4j.catalog.jpa.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.apache.logging.log4j.catalog.api.constant.Constants.DEFAULT_CATALOG;

@RestController
@RequestMapping(value = "/catalog")
public class CatalogController {

    private static final Logger LOGGER = LogManager.getLogger(CatalogController.class);

    private ModelMapper attributeModelMapper = new ModelMapper();
    private ModelMapper eventModelMapper = new ModelMapper();
    private ModelMapper productModelMapper = new ModelMapper();
    private ModelMapper categoryModelMapper = new ModelMapper();

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private AttributeModelConverter attributeModelConverter;

    @Autowired
    private AttributeConverter attributeConverter;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventModelConverter eventModelConverter;

    @Autowired
    private EventConverter eventConverter;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductModelConverter productModelConverter;

    @Autowired
    private ProductConverter productConverter;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private CategoryModelConverter categoryModelConverter;

    @Autowired
    private CategoryConverter categoryConverter;

    @PostConstruct
    public void init() {
        attributeModelMapper.addConverter(attributeModelConverter);
        eventModelMapper.addConverter(eventModelConverter);
        productModelMapper.addConverter(productModelConverter);
        categoryModelMapper.addConverter(categoryModelConverter);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "List catalog Attributes", notes = "List catalog attributes for a catalog id", tags = {"Catalog"})
    @GetMapping(value = "{catalogId}/attributes")
    public ResponseEntity<List<Attribute>> getAttributes(@ApiParam(value = "catalog id", required = true) @PathVariable String catalogId,
                                                         @RequestParam(value = "startIndex", required = false) Integer startIndex,
                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                         @RequestParam(value = "sortCol", required= false) String sortColumn,
                                                         @RequestParam(value = "sortDir", required = false) String sortDirection) {
        Type listType = new TypeToken<List<Attribute>>() {
        }.getType();
        List<Attribute> attributes = null;
        if (startIndex == null || pageSize == null) {
            attributes = attributeModelMapper.map(attributeService.getAttributes(catalogId), listType);
        } else {
            sortDirection = validateSortDirection(sortDirection);
            if (sortColumn == null || sortColumn.length() == 0) {
                sortColumn = "name";
            }
            int startPage = 0;
            if (startIndex > 0) {
                startPage = startIndex / pageSize;
            }
            attributes = attributeModelMapper.map(attributeService.getAttributes(startPage, pageSize, sortColumn,
                    sortDirection), listType);
        }
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        return new ResponseEntity<>(attributes, HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Create a catalog Attribute", notes = "Returns a catalog attribute", tags = {"Catalog"})
    @GetMapping(value = "{catalogId}/attribute/{name}")
    public ResponseEntity<Attribute> getAttribute(@ApiParam(value = "catalog id", required = true) @PathVariable String catalogId,
                                                         @ApiParam(value = "attribute name", required = true) @PathVariable String name) {
        Optional<AttributeModel> optional = attributeService.getAttribute(catalogId, name);
        if (!optional.isPresent()) {
            LOGGER.warn("Unable to locate attribute {} in catalog {}", name, catalogId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Attribute attribute = attributeModelConverter.convert(optional.get());
        return new ResponseEntity<>(attribute, HttpStatus.OK);
    }



    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Create a catalog Attribute", notes = "Creates a catalog attribute", tags = {"Catalog"})
    @PostMapping(value = "/attribute", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Attribute> createAttribute(@ApiParam(value = "attribute", required = true) @RequestBody Attribute attribute) {
        if (attribute.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required.");
        }
        if (DEFAULT_CATALOG.equals(attribute.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        Optional<AttributeModel> opt = attributeService.getAttribute(attribute.getCatalogId(), attribute.getName());
        if (opt != null && opt.isPresent()) {
            throw new IllegalStateException("An attribute named "+ attribute.getName() + " in catalog " +
                    attribute.getCatalogId() + " already exists");
        }
        AttributeModel model = attributeConverter.convert(attribute);
        model = attributeService.saveAttribute(model);
        return new ResponseEntity<>(attributeModelConverter.convert(model), HttpStatus.CREATED);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Update a catalog Attribute", notes = "Updates a catalog attribute", tags = {"Catalog"})
    @PutMapping(value = "/attribute", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Attribute> updateAttribute(@ApiParam(value = "attribute", required = true) @RequestBody Attribute attribute) {
        if (attribute.getId() == null) {
            throw new IllegalArgumentException("An Attribute must have an id to be updated.");
        }
        if (attribute.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required in the Attribute.");
        }
        if (DEFAULT_CATALOG.equals(attribute.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        AttributeModel model = attributeConverter.convert(attribute);
        model = attributeService.saveAttribute(model);
        return new ResponseEntity<>(attributeModelConverter.convert(model), HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Deletes a catalog Attribute", notes = "Deletes a catalog attribute", tags = {"Catalog"})
    @DeleteMapping(value = "/attribute/{id}")
    public ResponseEntity<?> deleteAttribute(@RequestParam("id") Long attributeId) {
        attributeService.deleteAttribute(attributeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "List catalog Events", notes = "Lists catalog events for a catalog id", tags = {"Catalog"})
    @GetMapping(value = "{catalogId}/events")
    public ResponseEntity<List<Event>> getEvents(@ApiParam(value = "catalog id", required = true) @PathVariable String catalogId,
                                                         @RequestParam(value = "startIndex", required = false) Integer startIndex,
                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                         @RequestParam(value = "sortCol", required= false) String sortColumn,
                                                         @RequestParam(value = "sortDir", required = false) String sortDirection) {
        Type listType = new TypeToken<List<Event>>() {}.getType();
        List<Event> events = null;
        if (startIndex == null || pageSize == null) {
            events = eventModelMapper.map(eventService.getEvents(catalogId), listType);
        } else {
            sortDirection = validateSortDirection(sortDirection);
            if (sortColumn == null || sortColumn.length() == 0) {
                sortColumn = "name";
            }
            int startPage = 0;
            if (startIndex > 0) {
                startPage = startIndex / pageSize;
            }
            events = eventModelMapper.map(eventService.getEvents(startPage, pageSize, sortColumn,
                    sortDirection), listType);
        }
        if (events == null) {
            events = new ArrayList<>();
        }
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Create a catalog Event", notes = "Creates a catalog event", tags = {"Catalog"})
    @PostMapping(value = "/event", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Event> createEvent(@ApiParam(value = "event", required = true) @RequestBody Event event) {
        if (event.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required to create an event.");
        }
        if (DEFAULT_CATALOG.equals(event.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        Optional<EventModel> opt = eventService.getEvent(event.getCatalogId(), event.getName());
        if (opt != null && opt.isPresent()) {
            throw new IllegalStateException("An event named "+ event.getName() + " in catalog " +
                    event.getCatalogId() + " already exists");
        }
        EventModel model = eventConverter.convert(event);
        model = eventService.saveEvent(model);
        return new ResponseEntity<>(eventModelConverter.convert(model), HttpStatus.CREATED);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Update a catalog Event", notes = "Updates a catalog event", tags = {"Catalog"})
    @PutMapping(value = "/event", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Event> updateEvent(@ApiParam(value = "event", required = true) @RequestBody Event event) {
        if (event.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required to update an event.");
        }
        if (DEFAULT_CATALOG.equals(event.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        EventModel model = eventConverter.convert(event);
        model = eventService.saveEvent(model);
        return new ResponseEntity<>(eventModelConverter.convert(model), HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Deletes a catalog event", notes = "Deletes a catalog event", tags = {"Catalog"})
    @DeleteMapping(value = "/event/{id}")
    public ResponseEntity<?> deleteEvent(@RequestParam("id") Long eventId) {
        eventService.deleteEvent(eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "List catalog Products", notes = "Lists catalog products for a catalog id", tags = {"Catalog"})
    @GetMapping(value = "{catalogId}/products")
    public ResponseEntity<List<Product>> getProducts(@ApiParam(value = "catalog id", required = true) @PathVariable String catalogId,
                                                 @RequestParam(value = "startIndex", required = false) Integer startIndex,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                 @RequestParam(value = "sortCol", required= false) String sortColumn,
                                                 @RequestParam(value = "sortDir", required = false) String sortDirection) {
        Type listType = new TypeToken<List<Product>>() {}.getType();
        List<Product> products = null;
        if (startIndex == null || pageSize == null) {
            products = productModelMapper.map(productService.getProducts(catalogId), listType);
        } else {
            sortDirection = validateSortDirection(sortDirection);
            if (sortColumn == null || sortColumn.length() == 0) {
                sortColumn = "name";
            }
            int startPage = 0;
            if (startIndex > 0) {
                startPage = startIndex / pageSize;
            }
            products = productModelMapper.map(productService.getProducts(startPage, pageSize, sortColumn,
                    sortDirection), listType);
        }
        if (products == null) {
            products = new ArrayList<>();
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Create a catalog Product", notes = "Creates a catalog product", tags = {"Catalog"})
    @PostMapping(value = "/product", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Product> createProduct(@ApiParam(value = "product", required = true) @RequestBody Product product) {
        if (product.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required to create a product.");
        }
        if (DEFAULT_CATALOG.equals(product.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        Optional<ProductModel> opt = productService.getProduct(product.getCatalogId(), product.getName());
        if (opt != null && opt.isPresent()) {
            throw new IllegalStateException("A product named "+ product.getName() + " in catalog " +
                    product.getCatalogId() + " already exists");
        }
        ProductModel model = productConverter.convert(product);
        model = productService.saveProduct(model);
        return new ResponseEntity<>(productModelConverter.convert(model), HttpStatus.CREATED);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Update a catalog Product", notes = "Updates a catalog event", tags = {"Catalog"})
    @PutMapping(value = "/product", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Product> updateProduct(@ApiParam(value = "product", required = true) @RequestBody Product product) {
        if (product.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required to update a product.");
        }
        if (DEFAULT_CATALOG.equals(product.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        ProductModel model = productConverter.convert(product);
        model = productService.saveProduct(model);
        return new ResponseEntity<>(productModelConverter.convert(model), HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Deletes a catalog product", notes = "Deletes a catalog product", tags = {"Catalog"})
    @DeleteMapping(value = "/product/{id}")
    public ResponseEntity<?> deleteProduct(@RequestParam("id") Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "List catalog Categories", notes = "Lists catalog categories for a catalog id", tags = {"Catalog"})
    @GetMapping(value = "{catalogId}/categories")
    public ResponseEntity<List<Category>> getCategories(@ApiParam(value = "catalog id", required = true) @PathVariable String catalogId,
                                                     @RequestParam(value = "startIndex", required = false) Integer startIndex,
                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                     @RequestParam(value = "sortCol", required= false) String sortColumn,
                                                     @RequestParam(value = "sortDir", required = false) String sortDirection) {
        Type listType = new TypeToken<List<Category>>() {}.getType();
        List<Category> categories = null;
        if (startIndex == null || pageSize == null) {
            categories = categoryModelMapper.map(categoryService.getCategories(catalogId), listType);
        } else {
            sortDirection = validateSortDirection(sortDirection);
            if (sortColumn == null || sortColumn.length() == 0) {
                sortColumn = "name";
            }
            int startPage = 0;
            if (startIndex > 0) {
                startPage = startIndex / pageSize;
            }
            categories = categoryModelMapper.map(categoryService.getCategories(startPage, pageSize, sortColumn,
                    sortDirection), listType);
        }
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Create a catalog Category", notes = "Creates a catalog category", tags = {"Catalog"})
    @PostMapping(value = "/category", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Category> createCategory(@ApiParam(value = "category", required = true) @RequestBody Category category) {
        if (category.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required to create a category.");
        }
        if (DEFAULT_CATALOG.equals(category.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        Optional<CategoryModel> opt = categoryService.getCategory(category.getCatalogId(), category.getName());
        if (opt != null && opt.isPresent()) {
            throw new IllegalStateException("A category named "+ category.getName() + " in catalog " +
                    category.getCatalogId() + " already exists");
        }
        CategoryModel model = categoryConverter.convert(category);
        model = categoryService.saveCategory(model);
        return new ResponseEntity<>(categoryModelConverter.convert(model), HttpStatus.CREATED);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Update a catalog Category", notes = "Updates a catalog category", tags = {"Catalog"})
    @PutMapping(value = "/category", consumes=Versions.V1_0_VALUE, produces=Versions.V1_0_VALUE)
    public ResponseEntity<Category> updateCategory(@ApiParam(value = "category", required = true) @RequestBody Category category) {
        if (category.getCatalogId() == null) {
            throw new IllegalArgumentException("A catalog id is required to create a category.");
        }
        if (DEFAULT_CATALOG.equals(category.getCatalogId())) {
            throw new IllegalArgumentException("The default catalog cannot be modified at run time.");
        }
        CategoryModel model = categoryConverter.convert(category);
        model = categoryService.saveCategory(model);
        return new ResponseEntity<>(categoryModelConverter.convert(model), HttpStatus.OK);
    }

    @ApiImplicitParams( {@ApiImplicitParam(dataType = "String", name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Deletes a catalog category", notes = "Deletes a catalog category", tags = {"Catalog"})
    @DeleteMapping(value = "/category/{id}")
    public ResponseEntity<?> deleteCategory(@RequestParam("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String validateSortDirection(String sortDirection) {
        if (sortDirection == null) {
            sortDirection = "ASC";
        } else if (sortDirection != "ASC" && sortDirection != "DESC") {
            LOGGER.warn("Invalid sort direction {}, defaulting to ascending", sortDirection);
            sortDirection = "ASC";
        }
        return sortDirection;
    }
}
