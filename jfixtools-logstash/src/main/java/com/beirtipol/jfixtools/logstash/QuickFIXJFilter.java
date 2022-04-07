/*
 * Copyright (C) 2022  https://github.com/beirtipol
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

package com.beirtipol.jfixtools.logstash;

import co.elastic.logstash.api.*;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import com.beirtipol.jfixtools.ui.tree.message.FIXMessageTreeNode;
import com.beirtipol.jfixtools.ui.util.MessageUtils;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// class name must match plugin name
@LogstashPlugin(name = "quickfixj_filter")
public class QuickFIXJFilter implements Filter {
    public static final PluginConfigSpec<URI>     DICTIONARY_PATH_CONFIG = PluginConfigSpec.uriSetting("dictionaryURI", "FIX50SP2.xml");
    public static final PluginConfigSpec<String>  SOURCE_CONFIG          = PluginConfigSpec.stringSetting("source", "message");
    public static final PluginConfigSpec<String>  TARGET_CONFIG          = PluginConfigSpec.stringSetting("target", "fix_message");
    public static final PluginConfigSpec<Boolean> DROP_SOURCE_CONFIG     = PluginConfigSpec.booleanSetting("drop_source", true);

    private final String              id;
    private final URI                 dictionaryPath;
    private final String              sourceField;
    private final String              targetField;
    private final boolean             dropSource;
    private final NamedDataDictionary dictionary;


    public QuickFIXJFilter(String id, Configuration config, Context context) {
        this.id             = id;
        this.dictionaryPath = config.get(DICTIONARY_PATH_CONFIG);
        this.sourceField    = config.get(SOURCE_CONFIG);
        this.targetField    = config.get(TARGET_CONFIG);
        this.dropSource     = config.get(DROP_SOURCE_CONFIG);
        try {
            this.dictionary = new NamedDataDictionary(dictionaryPath.toString(), dictionaryPath.toString());
        } catch (ConfigError e) {
            throw new IllegalArgumentException("Could not initialize Filter with Dictionary from " + dictionaryPath, e);
        }
    }

    DataDictionary getDictionary() {
        return this.dictionary;
    }

    @Override
    public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
        for (Event e : events) {
            Object messageField = e.getField(sourceField);
            if (messageField instanceof String) {
                try {
                    Message message = new MessageUtils().parse(dictionary, (String) messageField);
                    Map<String, Object> jsonFIX = new LinkedHashMap<>();
                    new FIXMessageTreeNode(dictionary, message).addToJSON(jsonFIX);
                    e.setField(targetField, jsonFIX);
                    matchListener.filterMatched(e);
                    if (dropSource) {
                        e.remove(sourceField);
                    }
                } catch (InvalidMessage ex) {
                    e.setField(targetField, ex.getMessage());
                }


            }
        }
        return events;
    }

    @Override
    public Collection<PluginConfigSpec<?>> configSchema() {
        return List.of(DICTIONARY_PATH_CONFIG, SOURCE_CONFIG, TARGET_CONFIG, DROP_SOURCE_CONFIG);
    }

    @Override
    public String getId() {
        return this.id;
    }
}
