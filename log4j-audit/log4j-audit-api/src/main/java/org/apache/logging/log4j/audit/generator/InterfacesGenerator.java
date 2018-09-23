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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.audit.util.NamingUtils;
import org.apache.logging.log4j.catalog.api.Attribute;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.Constraint;
import org.apache.logging.log4j.catalog.api.ConstraintType;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.api.EventAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InterfacesGenerator {

    private static final Logger LOGGER = LogManager.getLogger(InterfacesGenerator.class);

    private static final String CONSTRAINT_IMPORT = "org.apache.logging.log4j.audit.annotation.Constraint";
    private static final String REQUIRED_IMPORT = "org.apache.logging.log4j.audit.annotation.Required";
    private static final String CONSTRAINTS_ATTR = ", constraints={";
    private static final String CONSTRAINT = "@Constraint(constraintType=\"%s\", constraintValue=\"%s\")";
    private static final String KEY = "key=\"";
    private static final String REQUIRED_ATTR = "required=true";
    private static final String REQUIRED = "@Required";

    private static final String REQUEST_CONTEXT_IMPORT = "org.apache.logging.log4j.audit.annotation.RequestContext";
    private static final String PARENT_IMPORT = "org.apache.logging.log4j.audit.AuditEvent";
    private static final String MAX_LENGTH_IMPORT = "org.apache.logging.log4j.audit.annotation.MaxLength";
    private static final String REQCTX_ANN = "@RequestContext(";

    private static final String PARENT_CLASS = "AuditEvent";

    private static final String REQCTX = "ReqCtx_";

    private static final String EVENT_ID = "eventID";

    private static final String EVENT_TYPE = "eventType";

    private static final String TIMESTAMP = "timeStamp";

    private static final String CONTEXT = "context";

    @Autowired
    private CatalogReader catalogReader;

    @Value("${packageName:org.apache.logging.log4j.audit.event}")
    private String packageName;

    @Value("${outputDirectory:target/generated-sources/log4j-audit}")
    private String outputDirectory;

    @Value("${maxKeyLength:32}")
    private int maxKeyLength;

    @Value("${enterpriseId:18060}")
    private int enterpriseId;

    @Value("${verbose:false}")
    private boolean verbose;

    public CatalogReader getCatalogReader() {
        return catalogReader;
    }

    public void setCatalogReader(CatalogReader catalogReader) {
        this.catalogReader = catalogReader;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setMaxKeyLength(int maxKeyLength) {
        this.maxKeyLength = maxKeyLength;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void generateSource() throws Exception {
        boolean errors = false;
        CatalogData catalogData = catalogReader.read();
        if (catalogData != null) {
            List<Event> events = catalogData.getEvents();
            Map<String, Attribute> requestContextAttrs = new HashMap<>();
            Map<String, Boolean> requestContextIsRequired = new HashMap<>();
            Map<String, Attribute> attributes = catalogReader.getAttributes();
            Map<String, String> importedTypes = new HashMap<>();
            boolean anyConstraints = false;
            for (Attribute attribute : attributes.values()) {
                if (attribute.isRequestContext()) {
                    String name = attribute.getName();
                    if (name.startsWith(REQCTX)) {
                        name = name.substring(REQCTX.length());
                    }
                    requestContextAttrs.put(name, attribute);
                    requestContextIsRequired.put(name, attribute.isRequired());
                }
            }
            for (Event event : events) {
                String maxLen = Integer.toString(enterpriseId);
                int maxNameLength = maxKeyLength - maxLen.length() - 1;
                if (event.getName().length() > maxNameLength) {
                    LOGGER.error("{} exceeds maximum length of {} for an event name", event.getName(), maxNameLength);
                    errors = true;
                    continue;
                }
                ClassGenerator classGenerator = new ClassGenerator(
                        NamingUtils.getClassName(event.getName()), outputDirectory);
                classGenerator.setClass(false);
                classGenerator.setPackageName(packageName);
                classGenerator.setParentClassName(PARENT_CLASS);
                classGenerator.setJavadocComment(event.getDescription());
                classGenerator.setVerbose(verbose);
                Set<String> imports = classGenerator.getImports();
                imports.add(PARENT_IMPORT);
                StringBuilder annotations = new StringBuilder();
                imports.add(MAX_LENGTH_IMPORT);
                annotations.append("@MaxLength(").append(maxKeyLength).append(")");

                List<EventAttribute> eventAttributes = event.getAttributes();
                boolean anyRequired = false;
                if (eventAttributes != null) {
                    for (EventAttribute eventAttribute : eventAttributes) {
                        Attribute attribute = attributes.get(eventAttribute.getName());
                        if (attribute == null) {
                            LOGGER.error("Unable to locate attribute name {}", eventAttribute.getName());
                            errors = true;
                            continue;
                        }
                        if (attribute.isRequestContext() && attribute.isRequired()) {
                            String name = eventAttribute.getName();
                            if (name.startsWith(REQCTX)) {
                                name = name.substring(REQCTX.length());
                            }
                            requestContextIsRequired.put(name, Boolean.TRUE);
                            continue;
                        }
                        String name = attribute.getName();

                        if (EVENT_ID.equals(name) || EVENT_TYPE.equals(name) || TIMESTAMP.equals(name)) {
                            continue;
                        }

                        if (name.indexOf('.') != -1) {
                            name = name.replaceAll("\\.", "");
                        }

                        if (name.indexOf('/') != -1) {
                            name = name.replaceAll("/", "");
                        }
                        if (name.length() > maxKeyLength) {
                            LOGGER.error("{} exceeds maximum length of {} for an attribute name", name, maxKeyLength);
                            errors = true;
                            continue;
                        }

                        String type = attribute.getDataType().getTypeName();

                        MethodDefinition definition = new MethodDefinition("void",
                                NamingUtils.getMutatorName(name));
                        if (!attribute.isRequestContext() && attribute.getDataType().getImportClass() != null) {
                            if (!importedTypes.containsKey(attribute.getDataType().getTypeName())) {
                                importedTypes.put(attribute.getDataType().getTypeName(), attribute.getDataType().getImportClass());
                            }
                        }
                        definition.addParameter(new Parameter(name, type, attribute.getDescription()));
                        definition.setInterface(true);
                        definition.setVisability("public");
                        definition.setJavadocComments(attribute.getDisplayName()
                                + " : " + attribute.getDescription());

                        StringBuilder buffer = new StringBuilder();
                        Set<Constraint> constraints = attribute.getConstraints();
                        boolean first = true;
                        if (attribute.isRequired() || eventAttribute.isRequired()) {
                            anyRequired = true;
                            buffer.append(REQUIRED);
                            first = false;
                        }
                        if (constraints != null && constraints.size() > 0) {
                            anyConstraints = true;
                            for (Constraint constraint : constraints) {
                                if (!first) {
                                    buffer.append("\n    ");
                                }
                                first = false;
                                appendConstraint(constraint, buffer);
                            }
                        }
                        if (buffer.length() > 0) {
                            definition.setAnnotation(buffer.toString());
                        }
                        classGenerator.addMethod(definition);

                    }
                }
                if (importedTypes.size() > 0) {
                    for (String className : importedTypes.values()) {
                        imports.add(className);
                    }
                }
                if (anyRequired) {
                    imports.add(REQUIRED_IMPORT);
                }
                boolean firstReqCtx = true;
                if (requestContextAttrs.size() > 0) {
                    imports.add(REQUEST_CONTEXT_IMPORT);
                    StringBuilder reqCtx = new StringBuilder();
                    for (Map.Entry<String, Attribute> entry : requestContextAttrs.entrySet()) {
                        if (!firstReqCtx) {
                            reqCtx.append(")\n");
                        }
                        firstReqCtx = false;
                        reqCtx.append(REQCTX_ANN);
                        reqCtx.append(KEY).append(entry.getKey()).append("\"");
                        Attribute attrib = entry.getValue();
                        String name = attrib.getName();
                        if (name.startsWith(REQCTX)) {
                            name = name.substring(REQCTX.length());
                        }
                        Boolean isRequired = null;
                        final String attrName = name;
                        if (event.getAttributes() != null) {
                            Optional<EventAttribute> optional = event.getAttributes().stream().filter(a -> attrName.equals(a.getName())).findFirst();
                            if (optional.isPresent()) {
                                isRequired = optional.get().isRequired();
                            }
                        }
                        if ((isRequired != null && isRequired) ||
                                (isRequired == null && requestContextIsRequired.get(name))) {
                            reqCtx.append(", ").append(REQUIRED_ATTR);
                        }
                        Set<Constraint> constraints =  entry.getValue().getConstraints();
                        if (constraints != null && constraints.size() > 0) {
                            anyConstraints = true;
                            reqCtx.append(CONSTRAINTS_ATTR);
                            boolean first = true;
                            for (Constraint constraint : constraints) {
                                if (!first) {
                                    reqCtx.append(", ");
                                }
                                first = false;
                                appendConstraint(constraint, reqCtx);
                            }
                            reqCtx.append("}");

                        }
                    }
                    reqCtx.append(")");
                    if (annotations.length() > 0) {
                        annotations.append("\n");
                    }
                    annotations.append(reqCtx.toString());
                }
                if (anyConstraints) {
                    imports.add(CONSTRAINT_IMPORT);
                }
                if (annotations.length() > 0) {
                    classGenerator.setAnnotations(annotations.toString());
                }
                classGenerator.generate();
            }
        }
        if (errors) {
            throw new IllegalStateException("Errors were encountered during code generation");
        }
    }

    void appendConstraint(Constraint constraint, StringBuilder buffer) {
        ConstraintType type = constraint.getConstraintType();
        // Add the escapes since they have been removed when converting the original data to a Java Strinng. They need to
        // be added back for use in the Constraint declaration.
        buffer.append(String.format(CONSTRAINT, type.getName(), constraint.getValue().replace("\\", "\\\\")));
    }
}
