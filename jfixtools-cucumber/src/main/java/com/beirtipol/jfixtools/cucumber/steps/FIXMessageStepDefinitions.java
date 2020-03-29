/*
 * Copyright (C) 2020  https://github.com/beirtipol
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.beirtipol.jfixtools.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Config;
import org.ini4j.Wini;
import org.junit.Assert;
import quickfix.*;
import quickfix.field.CheckSum;
import quickfix.mina.SessionConnector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FIXMessageStepDefinitions {
    private Map<String, Message>          messages   = new HashMap<>();
    private Map<String, Group>            groups     = new HashMap<>();
    private Map<String, SessionConnector> connectors = new HashMap<>();

    private SessionSettings createSessionSettings(DataTable table) throws IOException, ConfigError {
        Wini ini = new Wini();
        Config config = new Config();
        config.setStrictOperator(true);
        config.setEscape(false);
        ini.setConfig(config);

        table.asLists().stream().skip(1).forEach(row -> {
            ini.put(row.get(0), row.get(1), row.get(2));
        });

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ini.store(output);
        ini.store(System.out);
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        return new SessionSettings(input);
    }

    @And("we sleep for {int} seconds")
    public void weSleepForSeconds(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }

    @Given("an {string} read from {string}")
    public void anReadFrom(String sessionType, String sessionProperties) throws ConfigError {
        createSessionConnector(sessionType, new SessionSettings(sessionProperties));
    }

    @Given("an {string} with the following properties")
    public void anWithTheFollowingProperties(String sessionType, DataTable table) throws ConfigError, IOException {
        SessionSettings settings = createSessionSettings(table);
        createSessionConnector(sessionType, settings);
    }

    private void createSessionConnector(String sessionType, SessionSettings settings) throws ConfigError {
        MessageFactory messageFactory = new DefaultMessageFactory();
        MessageStoreFactory messageStoreFactory = new MemoryStoreFactory();
        SLF4JLogFactory logFactory = new SLF4JLogFactory(settings);
        Application application = new SimpleFIXApp();
        SessionConnector connector = null;
        switch (sessionType) {
            case "acceptor":
                connector = new SocketAcceptor(application, messageStoreFactory, settings, logFactory, messageFactory);
                break;
            case "initiator":
                connector = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
                break;
        }
        connector.start();
        connectors.put(sessionType, connector);
    }

    @Then("we wait {int} millis for the {string} to be logged on")
    public void weWaitForTheToBeLoggedOn(int millis, String sessionType) throws InterruptedException {
        int count = 0;
        int increment = 250;
        SessionConnector connector = connectors.get(sessionType);
        while (count < millis) {
            count += increment;
            if (connector.isLoggedOn()) {
                break;
            }
            Thread.sleep(increment);
        }
        Assert.assertTrue(connector + " was not logged on in " + millis + " millis", connector.isLoggedOn());
    }

    @And("{string} is sent using {string}")
    public void isSentUsing(String messageName, String sessionID) throws SessionNotFound {
        Session.sendToTarget(messages.get(messageName), new SessionID(sessionID));
    }

    @Then("the {string} logs off")
    public void theLogsOff(String sessionType) {
        connectors.get(sessionType).stop(true);
    }

    private class SimpleFIXApp implements Application {

        @Override
        public void onCreate(SessionID sessionId) {

        }

        @Override
        public void onLogon(SessionID sessionId) {
            log.info("onLogon: " + sessionId);
        }

        @Override
        public void onLogout(SessionID sessionId) {
            log.info("onLogout: " + sessionId);
        }

        @Override
        public void toAdmin(Message message, SessionID sessionId) {
            log.info("toAdmin: " + sessionId + ": " + message);
        }

        @Override
        public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
            log.info("fromAdmin: " + sessionId + ": " + message);
        }

        @Override
        public void toApp(Message message, SessionID sessionId) throws DoNotSend {
            log.info("toApp: " + sessionId + ": " + message);
        }

        @Override
        public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
            log.info("fromApp: " + sessionId + ": " + message);
        }
    }

    @Given("a new fix {string} message called {string}")
    public void aNewFixMessage(String fixVersion, String messageName) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Class<?> msgCLass = Class.forName("quickfix.fix" + fixVersion + ".Message");
        messages.put(messageName, (Message) msgCLass.getConstructor().newInstance());
    }

    @When("field {int} on the {string} of {string} is set to {string}")
    public void fieldOnTheMessagePartIsSetTo(int field, String messagePart, String messageName, String value) {
        Message message = messages.get(messageName);
        FieldMap fieldMap = getFieldMap(message, messagePart);
        fieldMap.setString(field, value);

    }

    @And("the checksum is recalculated on {string}")
    public void theChecksumIsRecalculatedOn(String messageName) {
        Message message = messages.get(messageName);
        int checksum = MessageUtils.checksum(message.toString());
        message.getTrailer().setField(new CheckSum(String.format("%03d", checksum)));
    }

    @Then("field {int} on the {string} of {string} should equal {string}")
    public void fieldOnTheOfShouldEqual(int field, String messagePart, String messageName, String expected) throws FieldNotFound {
        Message message = messages.get(messageName);
        FieldMap fieldMap = getFieldMap(message, messagePart);
        Assert.assertEquals(expected, fieldMap.getString(field));
    }

    private FieldMap getFieldMap(Message message, String messagePart) {
        FieldMap fieldMap;
        switch (messagePart) {
            case "body":
                fieldMap = message;
                break;
            case "header":
                fieldMap = message.getHeader();
                break;
            case "trailer":
                fieldMap = message.getTrailer();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown message part %s", messagePart));
        }
        return fieldMap;
    }


    @And("a new group {int} with separator {int} called {string}")
    public void aNewGroupWithSeparatorCalled(int groupNumber, int separator, String groupName) {
        groups.put(groupName, new Group(groupNumber, separator));
    }

    @And("field {int} on group {string} is set to {string}")
    public void fieldOnGroupIsSetTo(int field, String groupName, String value) {
        groups.get(groupName).setString(field, value);
    }

    @And("group {string} is added to the {string} of {string}")
    public void groupIsAddedTo(String groupName, String messagePart, String messageName) {
        getFieldMap(messages.get(messageName), messagePart).addGroup(groups.get(groupName));
    }

    @And("the following groups are created")
    public void theFollowingGroupsAreCreated(DataTable table) {
        table.asLists().stream().skip(1).forEach(row -> {
            String groupName = row.get(0);
            Group group = groups.getOrDefault(groupName, new Group(Integer.parseInt(row.get(1)), Integer.parseInt(row.get(2))));
            groups.put(groupName, group);
            fieldOnGroupIsSetTo(Integer.parseInt(row.get(3)), groupName, row.get(4));
        });
    }

    @And("the following groups are added to the following messages")
    public void theFollowingGroupsAreAddedToTheFollowingMessages(DataTable table) {
        table.asLists().stream().skip(1).forEach(row ->
                groupIsAddedTo(row.get(2), row.get(1), row.get(0))
        );
    }

    /**
     * WARNING: This must be the last step and should only be used for debugging as converting a message to a string recalculates the checksum. Go figure!
     *
     * @param messageName
     */
    @And("{string} is logged")
    public void isLogged(String messageName) {
        log.info(messages.get(messageName).toString());
    }

    @Given("a fix message is read from {string} called {string} using dictionary {string}")
    public void aFixMessageReadFrom(String file, String messageName, String dictionary) throws URISyntaxException, ConfigError, InvalidMessage, IOException {
        URL resource = getClass().getClassLoader().getResource(file);
        String text = Files.readString(Paths.get(resource.toURI()));
        Message message = MessageUtils.parse(new DefaultMessageFactory(), new DataDictionary(dictionary), text);
        messages.put(messageName, message);
    }

}
