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
package com.beirtipol.jfixtools.logging.file;

import com.beirtipol.jfixtools.logging.FIXLogRepository;
import com.beirtipol.jfixtools.logging.model.FIXLogEntry;
import com.beirtipol.jfixtools.logging.model.FIXLogEntryType;
import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@TestPropertySource(locations = "./application-test.properties")
public class FIXLogFilesConfigurationTest {
    @Autowired
    private FIXLogFilesConfiguration configuration;

    @Autowired
    private FIXLogFileService logFileService;

    @Test
    public void testWiring() {
        assertNotNull(configuration);
        assertThat(configuration.getLogSources()).hasSize(2);
        FIXLogFileConfiguration parser = configuration.getLogSources().get(0);
        assertNotNull(parser);
        assertThat(parser.getName()).isEqualTo("Initiator->Acceptor Messages");
        assertThat(parser.getPaths()).hasSize(1);
    }

    @Test
    public void testParsing() {
        LocalDateTime from = LocalDateTime.of(2020, 3, 21, 13, 52, 28);
        LocalDateTime to = LocalDateTime.of(2020, 3, 21, 13, 52, 29);

        FIXLogRepository initiatorRepo = logFileService.getFIXLogFileRepositories().get(0);
        assertThat(initiatorRepo.getSessionID().matches(FIXSessionID.builder()
                .senderCompID("Initiator")
                .targetCompID("Acceptor")
                .build()));
        List<? extends FIXLogEntry> incomingMessages = initiatorRepo.getEntriesBetween(from, to);
        assertThat(initiatorRepo.getLogEntryType()).isEqualTo(FIXLogEntryType.MESSAGE);
        assertThat(incomingMessages).hasSize(1);
        assertThat(incomingMessages.get(0).getSendercompid()).isEqualTo("Initiator");

        FIXLogRepository acceptorRepo = logFileService.getFIXLogFileRepositories().get(1);
        assertThat(acceptorRepo.getLogEntryType()).isEqualTo(FIXLogEntryType.MESSAGE);
        assertThat(acceptorRepo.getSessionID().matches(FIXSessionID.builder()
                .senderCompID("Acceptor")
                .targetCompID("Initiator")
                .build()));
        List<? extends FIXLogEntry> outgoingMessages = acceptorRepo.getEntriesBetween(from, to);
        assertThat(outgoingMessages).hasSize(1);
        assertThat(outgoingMessages.get(0).getSendercompid()).isEqualTo("Acceptor");
    }
}
