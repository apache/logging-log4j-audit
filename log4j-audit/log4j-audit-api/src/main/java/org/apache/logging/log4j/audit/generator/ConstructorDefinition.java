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

import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.audit.generator.Constants.PUBLIC;

public class ConstructorDefinition implements Comparable<ConstructorDefinition> {
    private String visability = PUBLIC;

    private String name;

    private List<Parameter> parameters = new ArrayList<>();

    private List<String> exceptions = new ArrayList<>();

    private String content;

    public ConstructorDefinition(String className) {
        this.name = className;
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

    public String getVisability() {
        return visability;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setVisability(String visability) {
        this.visability = visability;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getVisability()).append(" ");
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

        sb.append(" {\n");
        sb.append(getContent());
        sb.append("\n}\n\n");
        return sb.toString();
    }

    @Override
    public int compareTo(ConstructorDefinition arg0) {
        return getParameters().size() - arg0.getParameters().size();
    }
}
