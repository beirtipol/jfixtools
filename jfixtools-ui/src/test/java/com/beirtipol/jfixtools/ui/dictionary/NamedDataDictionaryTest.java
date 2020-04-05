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

package com.beirtipol.jfixtools.ui.dictionary;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import quickfix.ConfigError;

import java.util.Arrays;
import java.util.stream.Stream;

public class NamedDataDictionaryTest {

    public static Stream<String> defaultDictionaries() {
        return Arrays.stream(NamedDataDictionary.DEFAULT_DICTIONARIES);
    }

    @ParameterizedTest(name = "Load dictionary for {0}")
    @MethodSource("defaultDictionaries")
    public void testDefaultDictionaries(String dictionary) throws ConfigError {
        NamedDataDictionary ndd = new NamedDataDictionary(dictionary, dictionary);
        Assertions.assertThat(ndd.getName()).isEqualTo(dictionary);
    }
}
