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
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FIXLogJPARepository implements FIXLogRepository {
    private FIXLogEntryType            logEntryType;
    private FIXSessionID               sessionID;
    private FIXMessageLogJPARepository messageRepository;
    private FIXEventLogJPARepository   eventRepository;

    @Override
    public List<? extends FIXLogEntry> getEntriesBetween(LocalDateTime from, LocalDateTime to) {
        switch (logEntryType) {
            case EVENT:
                return eventRepository.getEventsBetween(sessionID.getSenderCompID(), sessionID.getTargetCompID(), from, to);
            case MESSAGE:
                return messageRepository.getMessagesBetween(sessionID.getSenderCompID(), sessionID.getTargetCompID(), from, to);
            default:
                throw new UnsupportedOperationException("Unsupported LogEntry type");
        }

    }
}
