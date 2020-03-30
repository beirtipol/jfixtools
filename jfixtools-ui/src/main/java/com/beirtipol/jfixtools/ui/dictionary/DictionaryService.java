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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quickfix.ConfigError;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class DictionaryService {
    @Autowired
    private DictionaryConfig config;

    public Stream<NamedDataDictionary> loadCustom() {
        Stream<NamedDataDictionary> result = Stream.empty();
        for (String path : config.getPaths()) {
            try {
                Path dictionaryPath = Paths.get(getClass().getClassLoader().getResource(path).toURI());
                result = Stream.concat(result,
                        Files.list(dictionaryPath)
                                .filter(f -> f.toString().endsWith(".xml"))
                                .map(f -> {
                                    try (FileInputStream fis = new FileInputStream(f.toFile())) {
                                        return new NamedDataDictionary(f.getFileName().toString(), fis);
                                    } catch (IOException | ConfigError e) {
                                        log.error("Unable to load DataDictionary from " + f, e);
                                    }
                                    return null;
                                }).filter(dd -> dd != null));

            } catch (IOException | URISyntaxException | FileSystemNotFoundException e) {
                log.error("Could not access dictionaries in " + path, e);
            }
        }
        return result;
    }

    public Stream<NamedDataDictionary> loadDefault() {
        return Arrays.stream(NamedDataDictionary.DEFAULT_DICTIONARIES)
                .map(name -> {
                    try {
                        return new NamedDataDictionary(name);
                    } catch (ConfigError e) {
                        log.error("Error loading default dictionary for " + name, e);
                        return null;
                    }
                }).filter(dd -> dd != null);
    }

    public Stream<NamedDataDictionary> loadAll() {
        return Stream.concat(loadDefault(), loadCustom());
    }

    public Optional<NamedDataDictionary> loadDictionary(String name) {
        return loadAll().filter(dd -> dd.getName().equals(name)).findFirst();
    }
}
