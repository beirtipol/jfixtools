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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class FIXJPAService {
    @Autowired
    private FIXMessageLogRepository fixMessagesLogRepo;
    @Autowired
    private FIXEventLogRepository fixEventLogRepo;

    public enum LogEntry {
        MESSAGES_LOG, EVENT_LOG;
    }

    private Date convert(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.of("UTC")).toInstant());
    }

    public List<? extends FIXLogEntry> getLogEntriesBetween(LogEntry entry, String senderCompID, String targetCompID, LocalDateTime from, LocalDateTime to) {
        switch (entry) {
            case MESSAGES_LOG:
                return fixMessagesLogRepo.getMessagesBetween(senderCompID, targetCompID, from, to);
            case EVENT_LOG:
                return fixEventLogRepo.getEventsBetween(senderCompID, targetCompID, from, to);
            default:
                throw new UnsupportedOperationException("I do not know what type of entry this is: " + entry);
        }
    }
}
