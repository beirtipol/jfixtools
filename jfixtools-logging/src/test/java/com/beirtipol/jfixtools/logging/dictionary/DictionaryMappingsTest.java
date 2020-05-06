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

package com.beirtipol.jfixtools.logging.dictionary;

import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@TestPropertySource(locations = "./application-test.properties")
public class DictionaryMappingsTest {
    @Autowired
    DictionaryMappingService service;

    @Test
    public void loadMappings() {
        Optional<String> dictionaryName = service.dictionaryName(FIXSessionID.builder()
                .senderCompID("Initiator")
                .targetCompID("Acceptor")
                .build());
        assertThat(dictionaryName.isPresent()).isTrue();
        assertThat(dictionaryName.get()).isEqualTo("FIX44.xml");
    }
}
