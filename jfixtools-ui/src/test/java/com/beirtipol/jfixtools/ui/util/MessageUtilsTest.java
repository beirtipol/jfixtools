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

package com.beirtipol.jfixtools.ui.util;

import com.beirtipol.jfixtools.ui.dictionary.DictionaryConfig;
import com.beirtipol.jfixtools.ui.dictionary.DictionaryService;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import quickfix.Message;
import quickfix.field.SenderCompID;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {DictionaryConfig.class,DictionaryService.class, MessageUtils.class})
public class MessageUtilsTest {
    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private DictionaryService dictionaryService;
    private String expectedMessageText;

    @BeforeEach
    public void setup(){
        expectedMessageText = "8=FIX.4.4\u00019=72\u000135=A\u000134=5\u000149=Initiator\u000152=20200822-09:10:51.144\u000156=Acceptor\u000198=0\u0001108=30\u000110=224\u0001";
    }

    @Test
    public void correctChecksum() throws Exception {
        String brokenChecksum = "8=FIX.4.4\u00019=72\u000135=A\u000134=5\u000149=Initiator\u000152=20200822-09:10:51.144\u000156=Acceptor\u000198=0\u0001108=30\u000110=999\u0001";
        assertThat(messageUtils.correctChecksum(brokenChecksum)).isEqualTo(expectedMessageText);
    }

    @Test
    public void replacePipeWithSOH() throws Exception{
        String withPipe = "8=FIX.4.4|9=72|35=A|34=5|49=Initiator|52=20200822-09:10:51.144|56=Acceptor|98=0|108=30|10=224|";
        assertThat(messageUtils.replacePipeWithSOH(withPipe)).isEqualTo(expectedMessageText);
    }

    @Test
    public void parse() throws Exception{
        Optional<NamedDataDictionary> dictionary = dictionaryService.loadDictionary("FIX44.xml");
        Message message = messageUtils.parse(dictionary.get(), expectedMessageText);
        assertThat(message.getHeader().getString(SenderCompID.FIELD)).isEqualTo("Initiator");
    }


}

