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

package com.beirtipol.jfixtools.ui.tree.message;

import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldMapTreeNodeTest {
    @Test
    public void parseFixMessageToTree_checkOrderID() throws URISyntaxException, ConfigError, InvalidMessage, IOException {
        FIXMessageTreeNode tree = createMessageNode();

        List<FIXTreeNode> children = tree.getChildren();
        assertThat(children.size()).isEqualTo(3);
        assertThat(children.get(1).getChildren().stream().filter(node -> "11".equals(node.getTag())).findFirst().get().getValue()).isEqualTo("636730640278898634");
    }



    @Test
    public void toMap() throws URISyntaxException, ConfigError, InvalidMessage, IOException {
        FIXMessageTreeNode tree = createMessageNode();
        LinkedHashMap<String, Object> json = new LinkedHashMap<>();
        tree.addToJSON(json);
        List<FIXTreeNode> children = tree.getChildren();
        assertThat(children.size()).isEqualTo(3);
        assertThat(children.get(1).getChildren().stream().filter(node -> "11".equals(node.getTag())).findFirst().get().getValue()).isEqualTo("636730640278898634");
    }


    @Test
    public void nestedGroupJSON() throws URISyntaxException, ConfigError, InvalidMessage, IOException {
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

        FIXMessageTreeNode tree = new FIXMessageTreeNode(new NamedDataDictionary("FIX44.xml"), order);
        LinkedHashMap<String, Object> json = new LinkedHashMap<>();
        tree.addToJSON(json);
        assertThat(json.get("header")).satisfies(c -> {
            assertThat(((Map)c).size()).isEqualTo(2);
        });
    }

    private FIXMessageTreeNode createMessageNode() throws IOException, URISyntaxException, ConfigError, InvalidMessage {
        URL resource = getClass().getClassLoader().getResource("./fix/NewOrderSingle.fix");
        String text = Files.readString(Paths.get(resource.toURI()));
        NamedDataDictionary dataDictionary = new NamedDataDictionary("FIX44.xml");
        Message message = MessageUtils.parse(new DefaultMessageFactory(), dataDictionary, text);
        FIXMessageTreeNode tree = new FIXMessageTreeNode(dataDictionary, message);
        return tree;
    }
}
