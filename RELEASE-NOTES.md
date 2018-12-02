# Apache Log4j 1.0.1 Release Notes

The Apache Log4j Audit team is pleased to announce the Log4j Audit 1.0.1 release!

Apache Log4j Audit is a framework for performing audit logging using a predefined catalog of audit events. It
provides a tool to create and edit audit events. It also provides a REST service to perform the logging so
that non-Java applications can use a common auditing facility.

## Release 1.0.1

Changes in this version include:


### Fixed Bugs
* [LOG4J2-2428](https://issues.apache.org/jira/browse/LOG4J2-2428):
Use the AuditExceptionHandler for validation exceptions. Thanks to Andrei Ivanov.
* [LOG4J2-2443](https://issues.apache.org/jira/browse/LOG4J2-2443):
Fix inconsistencies in validation exceptions. Thanks to Andrei Ivanov.
* [LOG4J2-2440](https://issues.apache.org/jira/browse/LOG4J2-2440):
AuditEvents should provide some basic toString(). Thanks to Andrei Ivanov.
* [LOG4J2-2429](https://issues.apache.org/jira/browse/LOG4J2-2429):
Setting the exceptionHandler on the AuditEvent sets it as a ThreadContext variable. Thanks to Andrei Ivanov.
* [LOG4J2-2421](https://issues.apache.org/jira/browse/LOG4J2-2421):
Add verbose parameter to the Log4j audit Maven plugin. Thanks to Andrei Ivanov.
* [LOG4J2-2421](https://issues.apache.org/jira/browse/LOG4J2-2421):
AbstractEventLogger.logEvent doesn't check for missing required context attributes. Thanks to Andrei Ivanov.
* [LOG4J2-2417](https://issues.apache.org/jira/browse/LOG4J2-2417):
Better handling of optional properties. Thanks to Andrei Ivanov.
* [LOG4J2-2420](https://issues.apache.org/jira/browse/LOG4J2-2420):
RequestContextFilter logging cleanup. Thanks to Andrei Ivanov.
* [LOG4J2-2442](https://issues.apache.org/jira/browse/LOG4J2-2442):
Normalize the event names logged through AbstractEventLogger.logEvent. Thanks to Andrei Ivanov.
* [LOG4J2-2431](https://issues.apache.org/jira/browse/LOG4J2-2431):
Narrow the return type of getEvent. Thanks to Andrei Ivanov.


---

Apache Log4j Audit 1.0.1 requires a minimum of Java 8 to build and run.

For complete information on Apache Log4j Audit, including instructions on how to submit bug
reports, patches, or suggestions for improvement, see the Apache Apache Log4j Audit website:

http://logging.apache.org/log4j-audit