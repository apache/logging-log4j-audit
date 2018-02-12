<!-- vim: set syn=markdown : -->
<!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->

# Log4j Audit Maven Plugin

The Log4j Audit Maven Plugin generates Java Interfaces from the JSON catalog. Attributes that may be 
configured in this plugin include

<table>
<tr><th>Name</th><th>Type</th><th>Since</th><th>Required</th><th>Description</th><th>Default Value</th></tr>
<tr><td>catalogReaderClassName</td><td>String</td><td>-</td><td>false</td><td>CatalogReader used to read the 
JSON catalog</td><td>org.apache.logging.log4j.catalog.api.dao.FileCatalogReader</td></tr>
<tr><td>catalogReaderAttributes</td><td>Map</td><td>-</td><td>false</td><td>Parameters to pass to the CatalogReader</td>
<td></td></tr>
<tr><td>packageName</td><td>String</td><td>-</td><td>true</td><td>The package name to use for the generated Interfaces</td>
<td></td></tr>
<tr><td>outputDir</td><td>File</td><td>-</td><td>false</td><td>The root directory where the generated Interfaces should 
be created</td><td>${project.build.directory}/generated-sources/log4j-audit</td></tr>
<tr><td>maxKeyLength</td><td>int</td><td>-</td><td>false</td><td>The maximum length of attribute and event names.</td>
<td>32 - the maximum allowed to be compliant with RFC 5424</td></tr>
<tr><td>enterpriseId</td><td>int</td><td>-</td><td>false</td><td>The IANA enterprise id for RFC 5424 events</td>
<td>18060</td></tr>
</table>

Below is a sample configuration.

```
      <plugin>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-audit-maven-plugin</artifactId>
        <version>${log4j-audit.version}</version>
        <executions>
          <execution>
            <id>generate</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>generate</goal>
            </goals>
            <configuration>
              <packageName>org.apache.logging.log4j.audit.event</packageName>
              <enterpriseId>18060</enterpriseId>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

## Requirements

Log4j Audit requires Java 8.