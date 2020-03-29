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
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class FIXMessageLogEntryParserTest {

    @Test
    public void testParseHeartbeat() {
        FIXMessageLogEntryParser parser = new FIXMessageLogEntryParser();
        FIXLogEntry entry = parser.parse("8=FIX.4.4\u00019=71\u000135=A\u000134=1\u000149=Acceptor\u000152=20200321-13:52:28.199\u000156=Initiator\u000198=0\u0001108=1\u000110=178\u0001");
        assertThat(entry).isNotNull();
        assertThat(entry.getSendercompid()).isEqualTo("Acceptor");
        assertThat(entry.getTargetcompid()).isEqualTo("Initiator");
        assertThat(entry.getTime()).isEqualTo(LocalDateTime.of(2020, 3, 21, 13, 52, 28, 199000000));
    }
}
