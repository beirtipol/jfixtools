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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FIXLineStreamParserTest {
    @Test
    public void testJoinLines() throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("./com/beirtipol/jfixtools/logging/parsers/MultiLineFix.txt").toURI());
        Stream<String> lineStream = Files.lines(path);
        Stream<String> result = new FIXLineStreamParser().parse(lineStream);
        assertThat(result).hasSize(2);
    }
}
