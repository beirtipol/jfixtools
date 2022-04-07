/*
 * Copyright (C) 2022  https://github.com/beirtipol
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

package com.beirtipol.jfixtools.logstash;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.FilterMatchListener;
import org.apache.commons.jxpath.JXPathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.logstash.Event;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuickFIXJFilterTest {

    private QuickFIXJFilter createFilter(URI dictionary) {
        Configuration config = mock(Configuration.class);
        when(config.get(QuickFIXJFilter.DICTIONARY_PATH_CONFIG)).thenReturn(dictionary);
        when(config.get(QuickFIXJFilter.SOURCE_CONFIG)).thenReturn(QuickFIXJFilter.SOURCE_CONFIG.defaultValue());
        when(config.get(QuickFIXJFilter.TARGET_CONFIG)).thenReturn(QuickFIXJFilter.TARGET_CONFIG.defaultValue());
        when(config.get(QuickFIXJFilter.DROP_SOURCE_CONFIG)).thenReturn(QuickFIXJFilter.DROP_SOURCE_CONFIG.defaultValue());
        Context context = mock(Context.class);
        QuickFIXJFilter filter = new QuickFIXJFilter("Test", config, context);
        return filter;
    }

    @Test
    public void initializeWithClasspathDictionary() throws Exception {
        QuickFIXJFilter filter = createFilter(URI.create("FIX44.xml"));
        Assertions.assertEquals("FIX.4.4", filter.getDictionary().getVersion());
    }

    @Test
    public void initializeWithFileSystemDictionary() throws Exception {
        URI customDictionary = QuickFIXJFilterTest.class.getClassLoader().getResource("./CustomFIX44.xml").toURI();
        QuickFIXJFilter filter = createFilter(customDictionary);
        Assertions.assertEquals("FIX.4.4", filter.getDictionary().getVersion());
    }

    @Test
    public void convertFIXMessageWithSOHDelimiters() throws Exception {
        QuickFIXJFilter filter = createFilter(URI.create("FIX44.xml"));
        String message = Files.readString(Paths.get(QuickFIXJFilterTest.class.getClassLoader().getResource("./NewOrderSingle.fix").toURI()));
        Event event = new Event();
        event.setField("message", message);
        filter.filter(Collections.singletonList(event), mock(FilterMatchListener.class));
        JXPathContext context = JXPathContext.newContext(event.getField("fix_message"));

        Assertions.assertEquals("FIX.4.4", context.getValue("//BeginString"));
    }

    @Test
    public void convertFIXMessageWithNestedGroups() throws Exception {
        QuickFIXJFilter filter = createFilter(URI.create("FIX44.xml"));

        NewOrderSingle order = new NewOrderSingle();

        NewOrderSingle.NoPartyIDs parties = new NewOrderSingle.NoPartyIDs();
        parties.set(new PartyID("Party ID 1"));
        parties.set(new PartyIDSource(PartyIDSource.MIC));
        parties.set(new PartyRole(PartyRole.AGENT));
        NewOrderSingle.NoPartyIDs.NoPartySubIDs subID = new NewOrderSingle.NoPartyIDs.NoPartySubIDs();
        subID.set(new PartySubID("subid1@gmail.com"));
        subID.set(new PartySubIDType(PartySubIDType.EMAIL_ADDRESS));
        parties.addGroup(subID);
        subID = new NewOrderSingle.NoPartyIDs.NoPartySubIDs();
        subID.set(new PartySubID("SUB ID 1 App"));
        subID.set(new PartySubIDType(PartySubIDType.APPLICATION));
        parties.addGroup(subID);
        order.addGroup(parties);

        parties = new NewOrderSingle.NoPartyIDs();
        parties.set(new PartyID("Party ID 2"));
        parties.set(new PartyIDSource(PartyIDSource.BIC));
        parties.set(new PartyRole(PartyRole.AGENT));
        subID = new NewOrderSingle.NoPartyIDs.NoPartySubIDs();
        subID.set(new PartySubID("subid2@gmail.com"));
        parties.addGroup(subID);
        subID.set(new PartySubIDType(PartySubIDType.EMAIL_ADDRESS));
        subID = new NewOrderSingle.NoPartyIDs.NoPartySubIDs();
        subID.set(new PartySubID("SUB ID 2 App"));
        subID.set(new PartySubIDType(PartySubIDType.APPLICATION));
        parties.addGroup(subID);
        order.addGroup(parties);

        Event event = new Event();
        event.setField("message", order.toString());
        filter.filter(Collections.singletonList(event), mock(FilterMatchListener.class));
        System.out.println(event.toJson());
        JXPathContext context = JXPathContext.newContext(event.getField("fix_message"));

        Assertions.assertEquals("subid1@gmail.com", context.getValue("//NoPartyIDs[1]//NoPartySubIDs[1]/PartySubID[1]"));
    }

    @Test
    public void convertFIXMessageWithNonDictionaryField() throws Exception {
        QuickFIXJFilter filter = createFilter(URI.create("FIX44.xml"));
        NewOrderSingle msg = new NewOrderSingle();
        msg.setString(9999, "I am not a known field");
        Event event = new Event();
        event.setField("message", msg.toString());
        filter.filter(Collections.singletonList(event), mock(FilterMatchListener.class));
        JXPathContext context = JXPathContext.newContext(event.getField("fix_message"));

        // Can't use XPath syntax here as the key is a number
        Map<String, Object> value = (Map<String, Object>) context.getValue("/", Map.class);
        Assertions.assertEquals("I am not a known field", value.get("9999"));
    }

}
