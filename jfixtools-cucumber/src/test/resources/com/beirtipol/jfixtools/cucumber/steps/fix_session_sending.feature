Feature: FIX Session Creation
  Demonstrate ways to create a FIX session for sending and/or receiving messages

  Scenario: Initiator and Acceptor session created with DataTables
    Given an "acceptor" with the following properties
      | section | property                        | value                                    |
      | DEFAULT | BeginString                     | FIX.4.4                                  |
      | DEFAULT | DataDictionary                  | FIX44.xml                                |
      | DEFAULT | HeartBtInt                      | 1                                        |
      | DEFAULT | StartTime                       | 00:00:00                                 |
      | DEFAULT | EndTime                         | 23:59:59                                 |
      | DEFAULT | SLF4JLogHeartbeats              | Y                                        |
      | DEFAULT | SLF4JLogEventCategory           | ${senderCompID}.${targetCompID}.events   |
      | DEFAULT | SLF4JLogIncomingMessageCategory | ${senderCompID}.${targetCompID}.incoming |
      | DEFAULT | SLF4JLogOutgoingMessageCategory | ${senderCompID}.${targetCompID}.outgoing |
      | SESSION | ConnectionType                  | acceptor                                 |
      | SESSION | SocketConnectHost               | localhost                                |
      | SESSION | SocketAcceptPort                | 21000                                    |
      | SESSION | SenderCompID                    | Acceptor                                 |
      | SESSION | TargetCompID                    | Initiator                                |
    And an "initiator" with the following properties
      | section | property                        | value                                    |
      | DEFAULT | BeginString                     | FIX.4.4                                  |
      | DEFAULT | DataDictionary                  | FIX44.xml                                |
      | DEFAULT | HeartBtInt                      | 1                                        |
      | DEFAULT | StartTime                       | 00:00:00                                 |
      | DEFAULT | EndTime                         | 23:59:59                                 |
      | DEFAULT | SLF4JLogHeartbeats              | Y                                        |
      | DEFAULT | SLF4JLogEventCategory           | ${senderCompID}.${targetCompID}.events   |
      | DEFAULT | SLF4JLogIncomingMessageCategory | ${senderCompID}.${targetCompID}.incoming |
      | DEFAULT | SLF4JLogOutgoingMessageCategory | ${senderCompID}.${targetCompID}.outgoing |
      | SESSION | ConnectionType                  | initiator                                |
      | SESSION | SocketConnectHost               | localhost                                |
      | SESSION | SocketConnectPort               | 21000                                    |
      | SESSION | SenderCompID                    | Initiator                                |
      | SESSION | TargetCompID                    | Acceptor                                 |
    Then we wait 1500 millis for the "acceptor" to be logged on
    And we sleep for 2 seconds
    Then the "acceptor" logs off
    And the "initiator" logs off

  Scenario: Initiator and Acceptor session created from files
    Given an "acceptor" read from "./session/SampleAcceptor.properties"
    And an "initiator" read from "./session/SampleInitiator.properties"
    Then we wait 1500 millis for the "acceptor" to be logged on
    Then the "acceptor" logs off
    And the "initiator" logs off

  Scenario: Message sent and response inspected
    Given an "acceptor" read from "./session/SampleAcceptor.properties"
    And an "initiator" read from "./session/SampleInitiator.properties"
    Then we wait 1500 millis for the "acceptor" to be logged on
    Then a fix message is read from "./fix/NewOrderSingle.fix" called "NewOrderSingle" using dictionary "FIX44.xml"
    And "NewOrderSingle" is sent using "FIX.4.4:Initiator->Acceptor"
    Then the "acceptor" logs off
    And the "initiator" logs off