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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Responsible for converting streams of multiline fix messages in a stream of FIX messages
 */
public class FIXLineStreamParser {
    /**
     *
     * @param lines contains potentially multiline text messages. The beginning line of each message MUST begin with "8="
     * @return a reduced stream of one message per line
     */
    public Stream<String> parse(Stream<String> lines) {
        List<List<String>> result = new ArrayList<>();
        final AtomicInteger count = new AtomicInteger(0);
        lines.forEach(line -> {
            if (line.startsWith("8=")) {
                result.add(new ArrayList<>());
            }
            result.get(count.get()).add(line);
            if (Pattern.matches(".*?10=\\d+\u0001", line)) {
                count.getAndIncrement();
            }
        });
        return result.stream().flatMap(s -> Stream.of(s.stream().collect(Collectors.joining(""))));
    }
}
