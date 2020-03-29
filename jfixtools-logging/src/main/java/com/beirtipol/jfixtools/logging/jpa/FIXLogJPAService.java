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
import com.beirtipol.jfixtools.logging.model.FIXLogEntryType;
import com.beirtipol.jfixtools.logging.model.FIXSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FIXLogJPAService {

    @Autowired
    private FIXMessageLogJPARepository fixMessagesLogRepo;
    @Autowired
    private FIXEventLogJPARepository   fixEventLogRepo;
    @Autowired
    private FIXSessionJPARepository    sessionRepo;

    public List<FIXLogRepository> getFIXLogJPARepositories() {
        return getSessions().stream()
                .map(session -> FIXLogJPARepository.builder()
                        .eventRepository(fixEventLogRepo)
                        .messageRepository(fixMessagesLogRepo)
                        .sessionID(session.getId())
                ).map(builder -> Stream.of(
                        builder.logEntryType(FIXLogEntryType.EVENT).build(),
                        builder.logEntryType(FIXLogEntryType.MESSAGE).build()
                        )
                ).flatMap(s -> s)
                .collect(Collectors.toList());
    }

    public List<FIXSession> getSessions() {
        List<FIXSession> result = new ArrayList<>();
        for (FIXSession session : sessionRepo.findAll()) {
            result.add(session);
        }
        return result;
    }
}
