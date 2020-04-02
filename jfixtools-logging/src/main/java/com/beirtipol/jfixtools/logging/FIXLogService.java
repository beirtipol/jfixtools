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

package com.beirtipol.jfixtools.logging;

import com.beirtipol.jfixtools.logging.file.FIXLogFileService;
import com.beirtipol.jfixtools.logging.jpa.FIXLogJPAService;
import com.beirtipol.jfixtools.logging.model.FIXSession;
import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FIXLogService {
    @Autowired
    private FIXLogJPAService fixLogJPAService;

    @Autowired
    private FIXLogFileService fixLogFileService;

    public List<FIXLogRepository> getFIXLogRepositories() {
        return Stream.concat(
                fixLogJPAService.getFIXLogJPARepositories().stream(),
                fixLogFileService.getFIXLogFileRepositories().stream()
        ).collect(Collectors.toList());
    }

    public List<FIXSessionID> getSessionIDs() {
        return Stream.concat(
                fixLogJPAService.getSessions().stream()
                        .map(FIXSession::getId),
                fixLogFileService.getFIXLogFileRepositories().stream()
                        .map(FIXLogRepository::getSessionID))
                .collect(Collectors.toList());
    }
}
