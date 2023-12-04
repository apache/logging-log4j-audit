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

public class VariableDefinition implements Comparable<VariableDefinition> {
    private String visability;
    private boolean makeStatic = false;
    private boolean makeFinal = false;
    private boolean createGetter = false;
    private boolean createSetter = false;
    private String type;
    private String name;
    private String initialValue;
    private String annotation = null;

    public VariableDefinition(String visability, String type, String name, String initialValue) {
        this.visability = visability;
        this.type = type;
        this.name = name;
        this.initialValue = initialValue;
    }

    public VariableDefinition(String visability, String type, String name, String initialValue, String annotation) {
        this.visability = visability;
        this.type = type;
        this.name = name;
        this.initialValue = initialValue;
        this.annotation = annotation;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVisability() {
        return visability;
    }

    public boolean isCreateGetter() {
        return createGetter;
    }

    public boolean isCreateSetter() {
        return createSetter;
    }

    public boolean isMakeFinal() {
        return makeFinal;
    }

    public boolean isMakeStatic() {
        return makeStatic;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setCreateGetter(boolean createGetter) {
        this.createGetter = createGetter;
    }

    public void setCreateSetter(boolean createSetter) {
        this.createSetter = createSetter;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public void setMakeFinal(boolean makeFinal) {
        this.makeFinal = makeFinal;
    }

    public void setMakeStatic(boolean makeStatic) {
        this.makeStatic = makeStatic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVisability(String visability) {
        this.visability = visability;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getAnnotation() != null) {
            sb.append(getAnnotation());
            sb.append("\n");
        }

        sb.append(visability).append(" ");
        if (isMakeStatic()) {
            sb.append("static ");
        }
        if (isMakeFinal()) {
            sb.append("final ");
        }
        sb.append(type).append(" ").append(name);
        if (initialValue != null && initialValue.length() > 0) {
            sb.append(" = ").append(initialValue);
        }
        sb.append(";");

        // todo create getters and setters

        return sb.toString();
    }

    @Override
    public int compareTo(VariableDefinition arg0) {
        return getName().compareTo(arg0.getName());
    }
}
