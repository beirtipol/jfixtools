Feature: FIX Message Manipulation
  Demonstrate ways to create, load and modify FIX messages

  Scenario: New message with timestamp set and checksum recalculated
    Given a new fix "44" message called "fix44sample"
    When field 60 on the "body" of "fix44sample" is set to "2020-02-16T12:34:56.000000"
    And the checksum is recalculated on "fix44sample"
    Then field 10 on the "trailer" of "fix44sample" should equal "209"

  Scenario: FIX message read from file, timestamp set and checksum recalculated
    Given a fix message is read from "./fix/NewOrderSingle.fix" called "NewOrderSingle" using dictionary "FIX44.xml"
    Then field 10 on the "trailer" of "NewOrderSingle" should equal "092"
    When field 60 on the "body" of "NewOrderSingle" is set to "2020-02-16T12:34:56.000000"
    And the checksum is recalculated on "NewOrderSingle"
    Then field 10 on the "trailer" of "NewOrderSingle" should equal "078"

  Scenario: New message with repeating groups
    Given a new fix "44" message called "fix44WithRepeatingGroup"
    And a new group 268 with separator 269 called "md0"
    And field 269 on group "md0" is set to "0"
    And field 270 on group "md0" is set to "12.32"
    And field 271 on group "md0" is set to "100"
    And field 37 on group "md0" is set to "ORDERID"
    And group "md0" is added to the "body" of "fix44WithRepeatingGroup"
    And the following groups are created
      | groupName | groupNumber | groupSeparator | field | value   |
      | md1       | 268         | 269            | 269   | 1       |
      | md1       | 268         | 269            | 270   | 13.45   |
      | md1       | 268         | 269            | 271   | 200     |
      | md1       | 268         | 269            | 37    | ORDERID |
      | md2       | 268         | 269            | 269   | 2       |
      | md2       | 268         | 269            | 270   | 13.56   |
      | md2       | 268         | 269            | 271   | 300     |
      | md2       | 268         | 269            | 37    | ORDERID |
    And the following groups are added to the following messages
      | messageName             | messagePart | groupName |
      | fix44WithRepeatingGroup | body        | md1       |
      | fix44WithRepeatingGroup | body        | md2       |
    And the checksum is recalculated on "fix44WithRepeatingGroup"
    And "fix44WithRepeatingGroup" is logged

