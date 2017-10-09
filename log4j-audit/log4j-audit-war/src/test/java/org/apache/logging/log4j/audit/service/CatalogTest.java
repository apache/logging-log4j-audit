package org.apache.logging.log4j.audit.service;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.logging.log4j.audit.service.config.WebMvcAppContext;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebMvcAppContext.class}, loader=AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class CatalogTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testCreationOfANewProjectSucceeds() throws Exception {

        String result = mockMvc.perform(
                get("/catalog/TEST/attributes")
                        .accept(Versions.V1_0))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        assertNotNull("No result returned for TEST catalog", result);
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType typeReference =
                TypeFactory.defaultInstance().constructCollectionType(List.class, Attribute.class);
        List<Attribute> attributes = objectMapper.readValue(result, typeReference);
        assertNotNull("Result is not a list", attributes);
        assertEquals("Incorrect number of attributes in list", 0, attributes.size());
        result = mockMvc.perform(
                get("/catalog/DEFAULT/attributes")
                .accept(Versions.V1_0))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        assertNotNull("No result returned for DEFAULT catalog", result);

        attributes = objectMapper.readValue(result, typeReference);
        assertNotNull("Result is not a list", attributes);
        assertEquals("Incorrect number of attributes in list", 10, attributes.size());
    }
}
