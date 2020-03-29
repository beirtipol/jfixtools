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

import com.beirtipol.jfixtools.logging.model.FIXLogEntry;
import com.beirtipol.jfixtools.logging.model.FIXMessagesLogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@SpringBootTest
public class FIXMessagesLogJPARepositoryTest {
    @Autowired
    private FIXMessageLogJPARepository repository;

    @Test
    public void saveAndRetrieveByID() {
        FIXMessagesLogEntry message = FIXMessagesLogEntry.builder()
                .id(1l)
                .beginstring("FIX4.4")
                .sendercompid("Sender")
                .targetcompid("Target")
                .text("Some Text")
                .time(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .build();
        repository.save(message);
        FIXLogEntry foundEntity = repository.findById(message.getId()).get();

        assertNotNull(foundEntity);
        assertEquals(message, foundEntity);
    }

    @Test
    public void saveAndRetrieveByTime() {
        FIXMessagesLogEntry.FIXMessagesLogEntryBuilder<?, ?> builder = FIXMessagesLogEntry.builder()
                .beginstring("FIX4.4")
                .sendercompid("Sender")
                .targetcompid("Target")
                .text("Some Text");

        for (int hour = 0; hour < 10; hour++) {
            builder.id(hour);
            builder.time(LocalDateTime.of(2020, 1, 1, hour, 0, 0));
            repository.save(builder.build());
        }
        List<FIXMessagesLogEntry> messages = repository.getMessagesBetween("Sender", "Target",
                LocalDateTime.of(2020, 1, 1, 5, 30, 0),
                LocalDateTime.of(2020, 1, 1, 6, 30, 0));

        assertNotNull(messages);
        assertThat(messages.size()).isEqualTo(1);
    }
}
