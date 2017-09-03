# Apache Log4j Audit 

Log4j-Audit provides a framework for defining audit events and then logging them using Log4j. The framework focuses on 
defining the events and providing an easy mechanism for applications to log them, allowing products to provide 
consistency and validity to the events that are logged. It does not focus on how the log events are written to a
data store. Log4j itself provides many options for that.

The expected process is that business analysts or product managers will be managing the catalog, defining the events
and attributes so that consumers of the events will have a consistent experience. For example, instead of one event
using an attribute with a name of "custAddr" and another using a name of "customerAddress", both would use the same
name for attributes that have the same meaning. 

Once the catalog is modified in the web application it is saved as JSON to a git repository. It is expected that 
a continuous integration system would notice the change and start a build of that project. The build would use the 
Log4j Audit Maven Plugin to generate Java interfaces that Java developers would use to implement the audit logging
within the application. Non-Java applications would call the Audit Service by sending an appropriate Data Transfer
Object in JSON form and the Audit Service would perform the same validation that logging via the Java Interface
would.

Log4j Audit is not entirely usable by itself. Users of Log4j Audit must have a Git project that contains the JSON 
catalog which will contain the Java Interfaces for the events defined in the catalog after a build is performed. They
will need to modify the Audit Service provided with Log4j Audit to include the JSON catalog (by including the 
just discussed Git project as a dependency) and configuring the Audit Service as desired. The 
[Log4j Audit Samples](https://git-wip-us.apache.org/repos/asf?p=logging-log4j-audit-sample.git;a=tree) is a
sample project that illustrates how to perform these tasks.

## Packages

### Log4j Audit

The Log4j Audit module contains the Log4j Audit API that Java application leverage to perform audit logging, the
Log4j Audit Maven Plugin that generates Java Interfaces from the catalog, and the Audit Service that non-Java 
applications interact with to perform audit logging. The Audit service will also provide access to define 
dynamic catalog entries for systems that allow new custom objects and attributes to be created while the application 
is executing and need to audit when actions are taken against these objects. 

### Log4j Catalog 

The Log4j Catalog module contains the API for working with the catalog, a module for persisting the catalog using 
the Java Persistence Architecture (JPA), a module for reading and writing the JSON version of the catalog to a remote
GIT repository, and a module that provides a web application that can edit the various components of the catalog.

## Requirements

Log4j Audit requires a minimum of Java 8 and Log4j API and Core 2.9.

## Building From Source

Log4j Audit requires Apache Maven 3.x. To build from source and install to your local Maven repository, execute 
the following:

```sh
mvn clean install
```

## License

Apache Log4j Audit is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
