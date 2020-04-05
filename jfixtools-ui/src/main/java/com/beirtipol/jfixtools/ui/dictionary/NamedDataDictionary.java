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
import quickfix.ConfigError;
import quickfix.DataDictionary;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class NamedDataDictionary extends DataDictionary {
    public static final String   FIX40                = "FIX40.xml";
    public static final String   FIX41                = "FIX41.xml";
    public static final String   FIX42                = "FIX42.xml";
    public static final String   FIX43                = "FIX43.xml";
    public static final String   FIX44_MODIFIED       = "FIX44.modified.xml";
    public static final String   FIX44                = "FIX44.xml";
    public static final String   FIX50                = "FIX50.xml";
    public static final String   FIX50SPS1_MODIFIED   = "FIX50SP1.modified.xml";
    public static final String   FIX50SP1             = "FIX50SP1.xml";
    public static final String   FIX50SP2_MODIFIED    = "FIX50SP2.modified.xml";
    public static final String   FIX50SP2             = "FIX50SP2.xml";
    public static final String   FIXT11               = "FIXT11.xml";
    public static final String[] DEFAULT_DICTIONARIES = new String[]{FIX40, FIX41, FIX42, FIX43, FIX44_MODIFIED, FIX44, FIX50, FIX50SPS1_MODIFIED, FIX50SP1, FIX50SP2_MODIFIED, FIX50SP2, FIXT11};
    private             String   name;

    public NamedDataDictionary(String name) throws ConfigError {
        this(name, name);
    }

    public NamedDataDictionary(String name, InputStream in) throws ConfigError {
        super(in);
        this.name = name;
    }

    public NamedDataDictionary(String name, String location) throws ConfigError {
        super(location);
        this.name = name;
    }

    public NamedDataDictionary(String name, DataDictionary source) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Set<Integer>> getMessageFields() {
        Map<String, Set<Integer>> result = new HashMap<>();
        try {
            Field fld = DataDictionary.class.getDeclaredField("messageFields");
            fld.setAccessible(true);
            result = (Map<String, Set<Integer>>) fld.get(this);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            log.error("Could not access field values", e);
        }
        return result;
    }

    public Set<Integer> getMessageFields(String messageType) {
        Map<String, Set<Integer>> messageFields = getMessageFields();
        return messageFields.getOrDefault(messageType, new HashSet<>());
    }

}
