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

package com.beirtipol.jfixtools.logging.parsers;

import com.beirtipol.jfixtools.logging.model.FIXLogEntry;
import com.beirtipol.jfixtools.logging.model.FIXLogEntryType;
import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import lombok.*;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Data
@Builder
public class FIXMessageStreamParser {
    public static final  String MESSAGE_TOKEN   = "{message}";
    private static final String MESSAGE_PATTERN = "(8=.*?10=\\d+\u0001)";

    @Builder.Default
    private String pattern = MESSAGE_TOKEN;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Pattern                  linePattern;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private FIXMessageLogEntryParser parser = new FIXMessageLogEntryParser();

    private FIXLogEntryType logEntryType;

    private Pattern getLinePattern() {
        if (linePattern == null) {
            linePattern = Pattern.compile(pattern.replace("{message}", MESSAGE_PATTERN));
        }
        return linePattern;
    }

    public Stream<FIXLogEntry> filterStreamOfLines(Stream<String> lines, FIXSessionID sessionId, LocalDateTime from, LocalDateTime to) {
        return lines
                .map(line -> getLinePattern().matcher(line))
                .filter(matcher -> matcher.matches())
                .map(matcher -> matcher.group(1))
                .map(text -> parser.parse(text))
                .filter(entry -> sessionId.getSenderCompID().equals(entry.getSendercompid()))
                .filter(entry -> sessionId.getTargetCompID().equals(entry.getTargetcompid()))
                .filter(entry -> !from.isAfter(entry.getTime()) && !to.isBefore(entry.getTime()));
    }
}
