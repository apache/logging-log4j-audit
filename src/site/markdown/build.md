<!-- vim: set syn=markdown : -->
<!--
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements. See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

# Building and Installing Log4j Audit

*The information below is for developers who want to modify Log4j Audit or contribute
to the project. If your goal is to add logging to your application you don't need to
build from the source code, you can [download](download.html) the pre-built
binaries instead.*

Log4j Audit is hosted in the Apache Software Foundation's Git repository. Details on obtaining the
most current source code can be found at
[Log4j Audit Source Repository](source-repository.html). The source from the latest release may be
obtained by downloading it using the instructions at [Log4j Audit Downloads](download.html).

Log4j Audit uses Maven 3 as its build tool and uses the Java 8 compiler. 

To build and install Log4j Audit in your local Maven cache, from the parent project directory, and 
using Java 8, run: `mvn install`

Then to build the full site, you must use a local staging directory:

```
mvn site
[Windows] mvn site:stage-deploy -DstagingSiteURL=file:///%HOME%/log4j
[Unix] mvn site:stage-deploy -DstagingSiteURL=file:///$HOME/log4j
```

