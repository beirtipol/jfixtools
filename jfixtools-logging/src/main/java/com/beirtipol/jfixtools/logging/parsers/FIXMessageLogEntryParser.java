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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FIXMessageLogEntryParser {
    private static final Pattern           SENDER_PATTERN       = Pattern.compile(".*?49=(.*?)\u0001.*");
    private static final Pattern           TARGET_PATTERN       = Pattern.compile(".*?56=(.*?)\u0001.*");
    private static final Pattern           SENDING_TIME_PATTERN = Pattern.compile(".*?52=(.*?)\u0001.*");
    private static final DateTimeFormatter TAG52_FORMAT         = new DateTimeFormatterBuilder()
            .appendPattern("yyyyMMdd-HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

    @SuppressWarnings("rawtypes")
    public FIXLogEntry parse(String text) {
        FIXLogEntry.FIXLogEntryBuilder builder = FIXLogEntry.builder();
        Matcher sendingTime = SENDING_TIME_PATTERN.matcher(text);
        if (sendingTime.matches()) {
            LocalDateTime time = LocalDateTime.parse(sendingTime.group(1), TAG52_FORMAT);
            builder.time(time);
        }
        Matcher sender = SENDER_PATTERN.matcher(text);
        if (sender.matches()) {
            builder.sendercompid(sender.group(1));
        }
        Matcher target = TARGET_PATTERN.matcher(text);
        if (target.matches()) {
            builder.targetcompid(target.group(1));
        }
        return builder.text(text).build();
    }
}
