[![CircleCI](https://circleci.com/gh/beirtipol/jfixtools.svg?style=svg)](https://circleci.com/gh/beirtipol/jfixtools)

# JFIXTools

## Sample Application
The application.properties present in jfixtools-ui-vaadin demonstrates how to set up a source from both File and Database logs. 
It is important to note that the 'sessionID' part of the dictionary.mappings and the 'logSources' must contain the same fields
or else the dictionary will not be loaded when attempting to parse the text of a fix message.

## Configuration
### Files
A log file can contain messages for 1 or more sessions. In addition, it could contain event as well as message logs.

### DB
The standard QuickFixJ database table contain all messages in one table, all events in another.