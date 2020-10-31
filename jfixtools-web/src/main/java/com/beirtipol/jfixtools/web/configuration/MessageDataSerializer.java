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

package com.beirtipol.jfixtools.web.configuration;

import com.beirtipol.jfixtools.ui.field.MessageData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class MessageDataSerializer extends JsonSerializer<MessageData> {
    @Override
    public void serialize(MessageData messageData, JsonGenerator jg, SerializerProvider serializerProvider) throws IOException {
        jg.writeStartObject();
        jg.writeStringField("ID", messageData.getTag());
        jg.writeStringField("name", messageData.getName());
        jg.writeStringField("type", messageData.getType());
        jg.writeStringField("synopsis", messageData.getSynopsis());
        jg.writeStringField("elaboration", messageData.getElaboration());
        jg.writeStringField("added", messageData.getAdded());
        jg.writeStringField("deprecated", messageData.getDeprecated());
        jg.writeNumberField("count", 0);
        jg.writeEndObject();
    }
}
