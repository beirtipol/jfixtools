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
package com.beirtipol.jfixtools.logging.jpa;

import com.beirtipol.jfixtools.logging.FIXLogRepository;
import com.beirtipol.jfixtools.logging.model.FIXLogEntry;
import com.beirtipol.jfixtools.logging.model.FIXLogEntryType;
import com.beirtipol.jfixtools.logging.model.FIXMessagesLogEntry;
import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql({"/com/beirtipol/jfixtools/logging/jpa/data.sql"})
@ExtendWith({SpringExtension.class})
@SpringBootTest
public class FIXLogJPAServiceTest {
    @Autowired
    private FIXLogJPAService service;


    @Test
    public void loadTestDataAndRetrieveFromFilteredRepository() {
        List<FIXLogRepository> repositories = service.getFIXLogJPARepositories();
        FIXSessionID sessionID = FIXSessionID.builder()
                .senderCompID("Acceptor")
                .targetCompID("Initiator")
                .beginString("FIX.4.4")
                .build();
        Optional<FIXLogRepository> messageRepo = repositories.stream()
                .filter(f -> f.getLogEntryType() == FIXLogEntryType.MESSAGE)
                .filter(f -> f.getSessionID().matches(sessionID))
                .findFirst();
        List<? extends FIXLogEntry> messages = messageRepo.get().getEntriesBetween(
                LocalDateTime.of(2020, 4, 2, 9, 10, 52),
                LocalDateTime.of(2020, 4, 2, 9, 10, 54));

        assertNotNull(messages);
        assertThat(messages.size()).isEqualTo(2);
    }
}
