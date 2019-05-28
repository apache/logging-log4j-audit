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
package org.apache.logging.log4j.audit.generator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

public class TestInterfacesGenerator {
    private static Logger logger = LogManager.getLogger(TestInterfacesGenerator.class);
    private static ApplicationContext context;
    private static final String GENERATED_SOURCE_DIR = "target/generated-sources/log4j-audit/";

    @BeforeClass
    public static void initTest() {
        try {
            context = new ClassPathXmlApplicationContext("interfacesGeneratorContext.xml");
            assertNotNull("No application context", context);
        } catch (RuntimeException ex) {
            logger.error("Unable to create beans for interfacesGeneratorContext.xml", ex);
            throw ex;
        }
    }

    @Test
    public void testInterfaceGenerator() throws Exception {
        InterfacesGenerator interfacesGenerator =
                (InterfacesGenerator) context.getBean("interfacesGenerator");
        assertNotNull("No interfaces generator", interfacesGenerator);
        try {
            interfacesGenerator.generateSource();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        Path p = Paths.get(GENERATED_SOURCE_DIR);
        assertNotNull("Could not locate generated source path", p);
        int maxDepth = 10;
        List<String> fileNames = new ArrayList<>();
        Files.find(p, maxDepth, (path, basicFileAttributes) -> String.valueOf(path).endsWith(".java"))
                .map(path -> path.getFileName().toString()).forEach(fileNames::add);
        assertEquals("Incorrect number of files generated. Expected 4 was " + fileNames.size(), 4, fileNames.size());
    }
}
