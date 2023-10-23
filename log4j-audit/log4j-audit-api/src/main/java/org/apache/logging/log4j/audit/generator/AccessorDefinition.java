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

import org.apache.logging.log4j.audit.util.NamingUtils;
import org.apache.logging.log4j.audit.util.StringUtil;

public class AccessorDefinition {

    public class StandardGetter extends MethodDefinition {
        public StandardGetter(AccessorDefinition beanDefinition) {
            super(beanDefinition.getType(), NamingUtils.getAccessorName(
                    beanDefinition.getType(), beanDefinition.getName()));
            if (getterContent != null) {
                setContent("\t" + StringUtil.filterContent(getterContent, name, type));
            } else {
                setContent("\treturn " + beanDefinition.getName() + ";");
            }
        }
    }

    public class StandardSetter extends MethodDefinition {
        public StandardSetter(AccessorDefinition beanDefinition) {
            super(beanDefinition.getType(), NamingUtils
                    .getMutatorName(beanDefinition.getName()));
            setReturnType("void");
            if (setterContent != null) {
                setContent("\t"
                        + StringUtil.filterContent(setterContent, name, type));
            } else {
                String sb = "\tthis." + beanDefinition.getName() +
                        " = " + beanDefinition.getName() + ";";
                setContent(sb);
            }
            getParameters().add(new Parameter(beanDefinition.getName(), beanDefinition.getType(), ""));
        }
    }

    public static String variableCaseName(String variable) {
        return variable.substring(0, 1).toLowerCase() + variable.substring(1);
    }

    private String name;
    private String type;
    private String packageName = null;

    private String annotation = null;

    private String setterContent;
    private String getterContent;

    public void setSetterContent(String setterContent) {
        this.setterContent = setterContent;
    }

    public void setGetterContent(String getterContent) {
        this.getterContent = getterContent;
    }

    public AccessorDefinition(String name, String type) {
        this(name, type, null, null);
    }

    public AccessorDefinition(String name, String type, String setterContent, String getterContent) {

        setName(NamingUtils.getFieldName(name));
        setType(type);
        setSetterContent(setterContent);
        setGetterContent(getterContent);
    }

    public void addBean(ClassGenerator generator) {
        addBean(generator, true, true, true);
    }

    public void addBean(ClassGenerator generator, boolean addLocalVariable, boolean addGetter, boolean addSetter) {

        if (generator.isClass() && addLocalVariable) {
            generator.addLocalVariable(new VariableDefinition("private",
                    getType(), getName(), null, getAnnotation()));
        }

        if (packageName != null) {
            generator.getImports().add(packageName);
        }
        if (addGetter) {
            MethodDefinition methodDefinition = new StandardGetter(this);
            methodDefinition.setInterface(!generator.isClass());
            generator.addBeanMethods(this);
            generator.addMethod(methodDefinition);
        }
        if (addSetter) {
            MethodDefinition methodDefinition = new StandardSetter(this);
            methodDefinition.setInterface(!generator.isClass());
            generator.addMethod(methodDefinition);
        }
    }

    private String extractPackageName(String variable) {
        int lastDot = variable.lastIndexOf('.');
        if (lastDot < 0) {
            return null;
        }
        return variable;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setName(String name) {
        this.name = variableCaseName(name);
    }

    public void setType(String type) {
        this.packageName = extractPackageName(type);
        int lastDot = type.lastIndexOf('.');
        this.type = type;
        if (lastDot >= 0) {
            this.type = type.substring(lastDot + 1);
        }
    }

}
