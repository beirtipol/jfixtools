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
import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import org.junit.jupiter.api.BeforeAll;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql({"/com/beirtipol/jfixtools/logging/jpa/data.sql"})
@ExtendWith({SpringExtension.class})
@SpringBootTest
public class FIXLogJPAServiceTest {
    @Autowired
    private        FIXLogJPAService service;
    private static FIXSessionID     sessionID;
    private static LocalDateTime    fromDate;
    private static LocalDateTime    toDate;

    @BeforeAll
    public static void setup() {
        sessionID = FIXSessionID.builder()
                .senderCompID("Acceptor")
                .targetCompID("Initiator")
                .beginString("FIX.4.4")
                .build();

        fromDate = LocalDateTime.of(2020, 4, 2, 9, 10, 52);
        toDate   = LocalDateTime.of(2020, 4, 2, 9, 10, 54);
    }

    /**
     * The reason we put all of this in a single test is due to how the @Sql annotation is run before (or after) each test.
     * I could (should) make a cleandown script for the SQL but this will suffice for now.
     *
     * @see https://stackoverflow.com/questions/47775560/how-can-springs-test-annotation-sql-behave-like-beforeclass
     */
    @Test
    public void loadTestDataAndRetrieveFromFilteredRepository() {
        Optional<FIXLogRepository> messageRepo = findRepository(FIXLogEntryType.MESSAGE);
        List<? extends FIXLogEntry> messages = messageRepo.get().getEntriesBetween(fromDate, toDate);

        assertNotNull(messages);
        assertThat(messages.size()).isEqualTo(2);

        Optional<FIXLogRepository> eventRepo = findRepository(FIXLogEntryType.EVENT);
        List<? extends FIXLogEntry> events = eventRepo.get().getEntriesBetween(fromDate, toDate);

        assertNotNull(events);
        assertThat(events.size()).isEqualTo(1);
    }

    public Optional<FIXLogRepository> findRepository(FIXLogEntryType entryType) {
        List<FIXLogRepository> repositories = service.getFIXLogJPARepositories();
        return repositories.stream()
                .filter(f -> f.getLogEntryType() == entryType)
                .filter(f -> f.getSessionID().matches(sessionID))
                .findFirst();
    }
}
