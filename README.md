[![CircleCI](https://circleci.com/gh/beirtipol/jfixtools.svg?style=svg)](https://circleci.com/gh/beirtipol/jfixtools)

# JFIXTools

Like most projects, documentation is a work in progress. Here are some screenshots of the included 
VaadinApplication. You can simply clone and run to view the embedded Database and files with 
sample fix logs.

## Sample Application

### Screenshots
![Repository Search With Embedded Info](/docs/images/RepositorySearchWithEmbeddedInfo.png)


The application.properties present in jfixtools-ui-vaadin demonstrates how to set up a source from both File and Database logs. 
It is important to note that the 'sessionID' part of the dictionary.mappings and the 'logSources' must contain the same fields
or else the dictionary will not be loaded when attempting to parse the text of a fix message.



## Configuration
### Files
A log file can contain messages for 1 or more sessions. In addition, it could contain event as well as message logs.

### DB
The standard QuickFixJ database table contain all messages in one table, all events in another.