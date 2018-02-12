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

# Log4j Audit Service

The Log4j Audit Service WAR that allows remote applications to perform audit logging and to manage dynamic 
catalog entries. The generated war requires customization as shown in the 
[sample project](https://github.com/apache/logging-log4j-audit-sample/tree/master/audit-service-war). Once
that war is deployed navigating to http://localhost:8081/AuditService/swagger-ui.html in a web browser
will show the REST endpoints that may be accessed.

## Requirements

Log4j Audit requires Java 8.