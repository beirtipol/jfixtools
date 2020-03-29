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

package com.beirtipol.jfixtools.logging.file;

import com.beirtipol.jfixtools.logging.FIXLogRepository;
import com.beirtipol.jfixtools.logging.model.FIXLogEntry;
import com.beirtipol.jfixtools.logging.model.FIXLogEntryType;
import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import com.beirtipol.jfixtools.logging.parsers.FIXMessageStreamParser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Data
@Builder
public class FIXLogFileRepository implements FIXLogRepository {
    private FIXLogFileConfiguration config;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private FIXMessageStreamParser streamParser;

    private FIXMessageStreamParser getStreamParser() {
        if (streamParser == null) {
            streamParser = FIXMessageStreamParser.builder()
                    .pattern(config.getMessagePattern())
                    .build();
        }
        return streamParser;
    }

    @Override
    public FIXSessionID getSessionID() {
        return config.getSessionId();
    }

    @Override
    public FIXLogEntryType getLogEntryType() {
        return config.getEntryType();
    }

    @Override
    public List<? extends FIXLogEntry> getEntriesBetween(LocalDateTime from, LocalDateTime to) {
        Stream<String> textLines = config.getPaths().stream()
                .map(path -> {
                    try {
                        Path p = Paths.get(FIXLogFileConfiguration.class.getClassLoader().getResource(path).toURI());
                        return Files.lines(p, Charset.forName(config.getCharset()));
                    } catch (URISyntaxException | IOException e) {
                        log.error("Could not read files from: " + path);
                        return null;
                    }
                })
                .filter(lines -> lines != null)
                .flatMap(Function.identity());
        return getStreamParser().filterStreamOfLines(textLines, config.getSessionId(), from, to).collect(Collectors.toList());
    }
}
