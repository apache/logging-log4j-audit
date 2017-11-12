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
package org.apache.logging.log4j.audit.catalog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.Category;
import org.apache.logging.log4j.catalog.api.Constraint;
import org.apache.logging.log4j.catalog.api.DataType;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.EventAttribute;
import org.apache.logging.log4j.catalog.api.Product;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.plugins.MinValueConstraint;
import org.apache.logging.log4j.catalog.api.plugins.PatternConstraint;
import org.apache.logging.log4j.catalog.api.util.CatalogEventFilter;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class StringCatalogReader implements CatalogReader {
    private final String json;

    private final CatalogData catalogData;

    private final Map<String, Attribute> attributeMap = new HashMap<>();

    private final LocalDateTime lastUpdated;

    public StringCatalogReader() throws Exception {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper(factory);
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("catalogEvent", new CatalogEventFilter());
        mapper.setFilterProvider(filterProvider);
        catalogData = createCatalogData();
        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(catalogData);
        assertNotNull("No json catalog created", json);
        File file = new File("target/testCatalog.json");
        PrintStream ps = new PrintStream(new FileOutputStream(file));
        ps.print(json);
        ps.close();
        lastUpdated = LocalDateTime.now();
    }

    @Override
    public String readCatalog() {
        return json;
    }

    @Override
    public CatalogData read() {
        return catalogData;
    }

    @Override
    public Map<String, Attribute> getAttributes() {
        return attributeMap;
    }


    @Override
    public Attribute getAttribute(String name) {
        return attributeMap.get(name);
    }

    @Override
    public Category getCategory(String name) {
        if (catalogData.getCategories() != null) {
            return catalogData.getCategories().stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
        }
        return null;
    }


    @Override
    public Event getEvent(String name) {
        if (catalogData.getEvents() != null) {
            return catalogData.getEvents().stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
        }
        return null;
    }


    @Override
    public Product getProduct(String name) {
        if (catalogData.getProducts() != null) {
            return catalogData.getProducts().stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    private CatalogData createCatalogData() {
        CatalogData catalogData = new CatalogData();
        List<Product> products = new ArrayList<>();
        Product banking = new Product();
        banking.setName("banking").setDisplayName("Banking").setDescription("Fictional banking product");
        List<String> bankingEvents = new ArrayList<>();
        banking.setEvents(bankingEvents);
        products.add(banking);
        List<Category> categories = new ArrayList<>();
        Category accountCategory = new Category();
        accountCategory.setName("account").setDisplayName("Account").setDescription("Events related to accounts");
        categories.add(accountCategory);
        List<String> accountEvents = new ArrayList<>();
        accountCategory.setEvents(accountEvents);
        Category billPay = new Category();
        billPay.setName("billPay").setDisplayName("Bill Pay").setDescription("Events related to bill payment");
        List<String> billPayEvents = new ArrayList<>();
        billPay.setEvents(billPayEvents);
        categories.add(billPay);
        List<Attribute> attributes = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        Attribute attribute = new Attribute();
        attribute.setName("accountNumber").setDisplayName("Account Number").setDescription("Company account number");
        attribute.setDataType(DataType.INT).setIndexed(true).setSortable(true).setRequired(true).setRequestContext(true);
        attributes.add(attribute);
        attribute = new Attribute();
        attribute.setName("ipAddress").setDisplayName("IP Address").setDescription("IP Address of the caller");
        attribute.setDataType(DataType.STRING).setIndexed(true).setSortable(true).setRequired(false).setRequestContext(true);
        Set<Constraint> constraints = new HashSet<>();
        Constraint constraint = new Constraint();
        constraint.setConstraintType(new PatternConstraint()).setValue("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
        constraints.add(constraint);
        attribute.setConstraints(constraints);
        attributes.add(attribute);
        attribute = new Attribute();
        attribute.setName("userId").setDisplayName("UserId").setDescription("Id of the User").setDataType(DataType.INT);
        attribute.setIndexed(true).setSortable(true).setRequired(true).setRequestContext(true);
        attributes.add(attribute);
        attribute = new Attribute();
        attribute.setName("loginId").setDisplayName("LoginId").setDescription("Id user logs in with");
        attribute.setDataType(DataType.INT).setIndexed(true).setSortable(true).setRequired(true).setRequestContext(true);
        attributes.add(attribute);
        attribute = new Attribute();
        attribute.setName("hostName").setDisplayName("Host Name").setDescription("Name of the server");
        attribute.setDataType(DataType.STRING).setIndexed(true).setSortable(true).setRequired(false).setRequestContext(true);
        attributes.add(attribute);
        Attribute toAccount = new Attribute();
        toAccount.setName("toAccount").setDisplayName("To Account Number").setDescription("Destination account");
        toAccount.setDataType(DataType.INT).setIndexed(false).setSortable(false).setRequired(true).setRequestContext(false);
        constraints = new HashSet<>();
        constraint = new Constraint();
        constraint.setConstraintType(new MinValueConstraint()).setValue("1");
        constraints.add(constraint);
        toAccount.setConstraints(constraints);
        attributes.add(toAccount);
        Attribute fromAccount = new Attribute();
        fromAccount.setName("fromAccount").setDisplayName("From Account Number").setDescription("Source of funds");
        fromAccount.setDataType(DataType.INT).setIndexed(false).setSortable(false).setRequired(true).setRequestContext(false);
        attributes.add(fromAccount);
        Attribute amount = new Attribute();
        amount.setName("amount").setDisplayName("Amount").setDescription("Amount to transfer");
        amount.setDataType(DataType.BIG_DECIMAL).setIndexed(false).setSortable(false).setRequired(true).setRequestContext(false);
        attributes.add(amount);
        Attribute account = new Attribute();
        account.setName("account").setDisplayName("Account Number").setDescription("Accopunt number");
        account.setDataType(DataType.INT).setIndexed(false).setSortable(false).setRequired(true).setRequestContext(false);
        attributes.add(account);
        Attribute payee = new Attribute();
        payee.setName("payee").setDisplayName("Payee").setDescription("Recipient of payment");
        payee.setDataType(DataType.STRING).setIndexed(false).setSortable(false).setRequired(true).setRequestContext(false);
        attributes.add(payee);
        Event event = new Event();
        event.setName("login").setDisplayName("Login").setDescription("User Login");
        events.add(event);
        bankingEvents.add(event.getName());
        event = new Event();
        event.setName("transfer").setDisplayName("Transfer").setDescription("Transfer between accounts");
        List<EventAttribute> eventAttributes = new ArrayList<>();
        eventAttributes.add(new EventAttribute(toAccount.getName(), true));
        eventAttributes.add(new EventAttribute(fromAccount.getName(), true));
        eventAttributes.add(new EventAttribute(amount.getName(), true));
        event.setAttributes(eventAttributes);
        events.add(event);
        bankingEvents.add(event.getName());
        accountEvents.add(event.getName());
        event = new Event();
        event.setName("deposit").setDisplayName("Deposit").setDescription("Deposit funds");
        eventAttributes = new ArrayList<>();
        eventAttributes.add(new EventAttribute(account.getName(), true));
        eventAttributes.add(new EventAttribute(amount.getName(), true));
        event.setAttributes(eventAttributes);
        events.add(event);
        bankingEvents.add(event.getName());
        accountEvents.add(event.getName());
        event = new Event();
        event.setName("billPay").setDisplayName("Bill Pay").setDescription("Payment of a bill");
        eventAttributes = new ArrayList<>();
        eventAttributes.add(new EventAttribute(fromAccount.getName(), true));
        eventAttributes.add(new EventAttribute(payee.getName(), true));
        eventAttributes.add(new EventAttribute(amount.getName(), true));
        event.setAttributes(eventAttributes);
        events.add(event);
        billPayEvents.add(event.getName());
        bankingEvents.add(event.getName());
        catalogData.setAttributes(attributes);
        catalogData.setEvents(events);
        catalogData.setProducts(products);
        catalogData.setCategories(categories);
        for (Attribute attr : attributes) {
            attributeMap.put(attr.getName(), attr);
        }
        return catalogData;
    }
}
