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

# Log4j Audit Catalog JPA DAO

This module provides the DAOs to retrieve, update, delete, and save Log4j Audit Catalog elements in a relational
database.

The specific database used is determined by the jdbcUrl scheme provided. Log4j Audit currently supports HSQL and 
PostgresQL databases. Others may be added by providing a Spring configuration class that implements a method
named datasource that takes no arguments and returns a DataSource. The class must be annotated with the 
@JdbcUrl annotation with a parameter that matches the scheme specified in the JDBC Url String. See 
[PostgresqlDataSourceConfig.java](https://github.com/apache/logging-log4j-audit/blob/master/log4j-catalog/log4j-catalog-jpa/src/main/java/org/apache/logging/log4j/catalog/jpa/config/PostgresqlDataSourceConfig.java)
for an example.

## Requirements

Log4j Audit requires Java 8.