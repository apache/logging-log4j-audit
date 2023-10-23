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
package org.apache.logging.log4j.audit.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.audit.util.NamingUtils;
import org.apache.logging.log4j.audit.util.StringUtil;

import static org.apache.logging.log4j.audit.generator.Constants.PUBLIC;

public class MethodDefinition implements Comparable<MethodDefinition> {

    private String visibility = PUBLIC;

    private String name;

    private String returnType;

    private String annotation = null;

    private boolean isStatic = false;

    private boolean isFinal = false;

    private boolean isAbstract = false;

    private boolean isInterface = false;

    private final List<Parameter> parameters = new ArrayList<>();

    private List<String> exceptions = new ArrayList<>();

    private String content;

    private String javadocComments = null;

    public class StandardSingleton extends MethodDefinition {
        /**
         * this must be used with the local variable
         */
        public StandardSingleton(String returnType, String name, List<String> parameters) {
            super(returnType, name);
            setStatic(true);
            String prefix = "get";
            setName(prefix + name.substring(0, 1).toUpperCase()
                    + name.substring(1));
            StringBuilder sb = new StringBuilder();
            sb.append("\tif (").append(name).append(" == null) {\n");
            sb.append("\t\t").append(name).append(" = new ").append(returnType)
                    .append("(");
            boolean first = true;
            if (parameters != null) {
                for (String element : parameters) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(element);
                    first = false;
                }
            }
            sb.append(");\n\t}\n\treturn ").append(name).append(";");
            setContent(sb.toString());
        }
    }

    private static MethodDefinition definition = new MethodDefinition("dumb",
            "dumb");

    public static MethodDefinition getStandardSingleton(String returnType,
                                                        String name, List<String> parameters) {
        return definition.new StandardSingleton(returnType, name, parameters);
    }

    public MethodDefinition(String returnType, String name, String content) {
        this.returnType = returnType;
        this.name = name;
        if (content != null) {
            this.content = StringUtil.filterContent(content, name, returnType);
        } else {
            createStubContent();
        }
    }

    public MethodDefinition(String returnType, String name) {
        this(returnType, name, null);
    }

    private void createStubContent() {
        String content = "// default stub - please modify\n";
        setContent(content);
        if (!returnType.equals("void")) {

            if (returnType.equals("int")) {
                setContent(content + "return 0;");
            } else if (returnType.equals("boolean")) {
                setContent(content + "return false;");
            } else if (returnType.equals("double")) {
                setContent(content + "return 0.0;");
            } else if (returnType.equals("long")) {
                setContent(content + "return 0;");
            } else if (returnType.equals("float")) {
                setContent(content + "return 0.0;");
            } else if (returnType.equals("char")) {
                setContent(content + "return ' ';");
            } else if (returnType.equals("short")) {
                setContent(content + "return 0;");
            } else {
                setContent(content + "return null;");
            }
        }

    }

    public MethodDefinition(Method method) {
        this(method, null);
    }

    public MethodDefinition(Method method, String content) {
        this.returnType = method.getReturnType().getName();
        this.name = method.getName();

        if (content == null) {
            createStubContent();
        } else {
            this.content = content;
        }
        int pName = 'a';
        for (Class<?> param : method.getParameterTypes()) {
            addParameter(new Parameter(Character.toString((char) pName++),
                    param.getName(), ""));
        }

        for (Class<?> param : method.getExceptionTypes()) {
            exceptions.add(param.getName());
        }
    }

    public void addParameter(Parameter paramater) {
        parameters.add(paramater);
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getContent() {
        return content;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getVisability() {
        return visibility;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public void setVisability(String visability) {
        this.visibility = visability;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("    /**\n");
        if (getJavadocComments() != null) {
            sb.append("     * ").append(getJavadocComments());
        }

        if (getParameters() != null) {
            for (Parameter param : getParameters()) {
                sb.append("\n     * @param ").append(param.getName())
                        .append(" ").append(param.getDescription());
            }
        }

        sb.append("\n     */\n");
        sb.append("    ");
        if (getAnnotation() != null) {
            sb.append(getAnnotation());
            sb.append("\n    ");
        }

        if (getVisability() != null) {
            sb.append(getVisability()).append(" ");
        }
        if (isFinal() && !isInterface()) {
            sb.append("final ");
        }
        if (isStatic() && !isInterface()) {
            sb.append("static ");
        }
        if (isAbstract() && !isInterface()) {
            sb.append("abstract ");
        }
        sb.append(returnType).append(" ");
        sb.append(getName()).append("(");
        if (getParameters() != null) {
            boolean first = true;
            for (Parameter element : getParameters()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(element);
                first = false;
            }
        }
        sb.append(")");
        if (getExceptions() != null && getExceptions().size() > 0) {
            sb.append(" throws ");
            boolean first = true;
            for (String element : getExceptions()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(element);
                first = false;
            }
        }

        if (isAbstract() || isInterface()) {
            sb.append(";");
            return sb.toString();
        }
        sb.append(" {\n");
        sb.append(getContent());
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public int compareTo(MethodDefinition arg0) {
        int res = NamingUtils.getMethodShortName(getName()).compareTo(
                NamingUtils.getMethodShortName(arg0.getName()));
        if (res == 0) {
            return getName().compareTo(arg0.getName());
        }
        return res;
    }

    public String getJavadocComments() {
        return javadocComments;
    }

    public void setJavadocComments(String javadocComments) {
        this.javadocComments = javadocComments;
    }

}
