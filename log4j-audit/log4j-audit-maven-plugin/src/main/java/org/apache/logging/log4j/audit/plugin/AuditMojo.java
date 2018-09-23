/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.audit.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.audit.generator.InterfacesGenerator;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.dao.JsonCatalogReader;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Goal which generates the audit interfaces.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class AuditMojo extends AbstractMojo {

    private static final String BASEDIR = "baseDir";
    private static final String BUILDDIR = "buildDir";
    private static final int MAX_KEY_LENGTH = 32;
    private static final int DEFAULT_ENTERPRISE_ID = 18060;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "catalogReaderClassName", defaultValue = "org.apache.logging.log4j.catalog.api.dao.FileCatalogReader")
    private String catalogReaderClassName;

    @Parameter(property = "catalogReaderAttributes", required = false)
    private Map<String, String> catalogReaderAttributes;

    @Parameter(property = "packageName", required = true)
    private String packageName;
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/log4j-audit", property = "outputDir")
    private File outputDirectory;

    @Parameter(required = false)
    private int maxKeyLength;

    @Parameter(required = false)
    private int enterpriseId;

    /**
     * Set to <code>true</code> to show messages about what the code generator is doing.
     */
    @Parameter(defaultValue = "false")
    private boolean verbose;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException {
        if (maxKeyLength <= 0) {
            maxKeyLength = MAX_KEY_LENGTH;
        }
        if (enterpriseId <= 0) {
            enterpriseId = DEFAULT_ENTERPRISE_ID;
        }
        CatalogReader catalogReader = null;
        try {
            File basedir = project.getBasedir();
            Class<?> clazz = LoaderUtil.loadClass(catalogReaderClassName);
            Constructor<CatalogReader>[] constructors = (Constructor<CatalogReader>[]) clazz.getConstructors();

            for (Constructor<CatalogReader> constructor : constructors) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(Map.class)) {
                    if (catalogReaderAttributes == null) {
                        catalogReaderAttributes = new HashMap<>();
                    }
                    if (!catalogReaderAttributes.containsKey(BASEDIR)) {
                        catalogReaderAttributes.put(BASEDIR, project.getBasedir().getAbsolutePath());
                    }
                    if (!catalogReaderAttributes.containsKey(BUILDDIR)) {
                        catalogReaderAttributes.put(BUILDDIR, project.getBuild().getDirectory());
                    }
                    catalogReader = constructor.newInstance(catalogReaderAttributes);
                    break;
                }
            }
            if (catalogReader == null) {
                catalogReader = LoaderUtil.newInstanceOf(catalogReaderClassName);
            }

        } catch (Exception ex) {
            getLog().error("Unable to load catalog reader " + catalogReaderClassName, ex);
            return;
        }
        InterfacesGenerator generator = new InterfacesGenerator();
        JsonCatalogReader jsonCatalogReader = new JsonCatalogReader();
        jsonCatalogReader.setCatalogReader(catalogReader);
        jsonCatalogReader.init();
        generator.setCatalogReader(jsonCatalogReader);
        generator.setOutputDirectory(outputDirectory.getAbsolutePath());
        generator.setPackageName(packageName);
        generator.setMaxKeyLength(maxKeyLength);
        generator.setEnterpriseId(enterpriseId);
        generator.setVerbose(verbose);
        try {
            generator.generateSource();
            project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        } catch (Exception ex) {
            throw new MojoExecutionException("Error generating Audit interfaces", ex);
        }
    }
}
