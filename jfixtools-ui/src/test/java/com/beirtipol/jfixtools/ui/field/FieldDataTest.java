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

package com.beirtipol.jfixtools.ui.field;

import com.beirtipol.jfixtools.repository.FIXRepositoryConfig;
import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.repository.FixRepositoryJAXB;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import quickfix.ConfigError;
import quickfix.field.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {FIXRepositoryHelper.class, FixRepositoryJAXB.class})
@TestPropertySource(locations = "./application-test.properties")
@EnableConfigurationProperties({FIXRepositoryConfig.class})
public class FieldDataTest {
    @Autowired
    private FIXRepositoryHelper helper;


    public FieldData.FieldDataBuilder baseBuilder() throws ConfigError {
        return FieldData.builder()
                .dictionary(new NamedDataDictionary(NamedDataDictionary.FIX44))//
                .helper(this.helper);
    }

    @Test
    public void messageTypeField_noMessageType() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(MsgType.FIELD).build();
        assertThat(fieldData.getName()).isEqualTo("MsgType");
        assertThat(fieldData.getSynopsis()).isEqualTo("Defines message type ALWAYS THIRD FIELD IN MESSAGE. (Always unencrypted)\n" +
                "Note: A \"U\" as the first character in the MsgType field (i.e. U, U2, etc) indicates that the message format is privately defined between the sender and receiver.\n" +
                "*** Note the use of lower case letters ***");
        assertThat(fieldData.getType()).isEqualTo("STRING");
        assertThat(fieldData.getElaboration()).isEqualTo("");
    }

    @Test
    public void executionID_noMessageType() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(ExecID.FIELD).build();
        assertThat(fieldData.getName()).isEqualTo("ExecID");
        assertThat(fieldData.getSynopsis()).isEqualTo("Unique identifier of execution message as assigned by sell-side (broker, exchange, ECN) (will be 0 (zero) for ExecType (150)=I (Order Status)).\n" +
                "Uniqueness must be guaranteed within a single trading day or the life of a multi-day order. Firms which accept multi-day orders should consider embedding a date within the ExecID field to assure uniqueness across days.\n" +
                "(Prior to FIX 4.1 this field was of type int).");
        assertThat(fieldData.getType()).isEqualTo("STRING");
        assertThat(fieldData.required()).isFalse();
        assertThat(fieldData.getElaboration()).isEqualTo("");
    }

    @Test
    public void executionID_messageType() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(ExecID.FIELD).messageType("8").build();
        assertThat(fieldData.getName()).isEqualTo("ExecID");
        assertThat(fieldData.getSynopsis()).isEqualTo("Unique identifier of execution message as assigned by sell-side (broker, exchange, ECN) (will be 0 (zero) for ExecType (150)=I (Order Status)).\n" +
                "Uniqueness must be guaranteed within a single trading day or the life of a multi-day order. Firms which accept multi-day orders should consider embedding a date within the ExecID field to assure uniqueness across days.\n" +
                "(Prior to FIX 4.1 this field was of type int).");
        assertThat(fieldData.getType()).isEqualTo("STRING");
        assertThat(fieldData.required()).isTrue();
        assertThat(fieldData.getElaboration()).isEqualTo("");
        assertThat(fieldData.getAdded()).isEqualTo("FIX.2.7");
        assertThat(fieldData.getDeprecated()).isBlank();
    }

    @Test
    public void noPartyIDs_withMessageType_isGroup() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(NoPartyIDs.FIELD).messageType("8").build();
        assertThat(fieldData.getName()).isEqualTo("NoPartyIDs");
        assertThat(fieldData.isGroup()).isTrue();

    }

    @Test
    public void noSides_withoutMessageType_isNotGroup() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(NoPartyIDs.FIELD).build();
        assertThat(fieldData.getName()).isEqualTo("NoPartyIDs");
        assertThat(fieldData.isGroup()).isFalse();
    }

    @Test
    public void nonEnumFields() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(Account.FIELD).build();
        assertThat(fieldData.isEnum()).isFalse();
    }

    @Test
    public void javaType() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(Account.FIELD).build();
        assertThat(fieldData.getJavaType()).isEqualTo(String.class);
    }

    @Test
    public void enumFields() throws Exception {
        FieldData fieldData = baseBuilder().tagNum(CommType.FIELD).build();
        assertThat(fieldData.isEnum()).isTrue();
        EnumData perUnit = fieldData.getEnums().stream().filter(ed -> "1".equals(ed.getName())).findFirst().get();
        assertThat(perUnit.getDescription()).isEqualTo("PER_UNIT");
        assertThat(perUnit.getSynopsis()).isEqualTo("Amount per unit");
        assertThat(perUnit.getElaboration()).isEqualTo("Implying shares, par, currency, physical unit etc. Use CommissionUnitOfMeasure(1238) to clarify for commodities.");
        assertThat(perUnit.getDeprecated()).isBlank();
        assertThat(perUnit.getAdded()).isEqualTo("FIX.2.7");
    }
}

