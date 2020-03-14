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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface FIXEventLogRepository extends CrudRepository<FIXEventLogEntry, Long> {

    @Query("SELECT le from com.beirtipol.jfixtools.logging.jpa.FIXEventLogEntry le where le.sendercompid = :sendercompid AND targetcompid = :targetcompid and le.time >= :from and le.time <= :to")
    List<FIXEventLogEntry> getEventsBetween(//
                                            @Param("sendercompid") String senderCompID, //
                                            @Param("targetcompid") String targetCompID, //
                                            @Param("from") LocalDateTime from, //
                                            @Param("to") LocalDateTime to);
}
