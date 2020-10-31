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

import com.beirtipol.jfixtools.ui.field.EnumData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class EnumDataSerializer extends JsonSerializer<EnumData> {
    @Override
    public void serialize(EnumData enumData, JsonGenerator jg, SerializerProvider serializerProvider) throws IOException {
        jg.writeStartObject();
        jg.writeStringField("ID", enumData.getTag() + "_" + enumData.getName());
        jg.writeStringField("name", enumData.getName());
        jg.writeStringField("synopsis", enumData.getSynopsis());
        jg.writeStringField("elaboration", enumData.getElaboration());
        jg.writeStringField("added", enumData.getAdded());
        jg.writeStringField("deprecated", enumData.getDeprecated());
        jg.writeEndObject();
    }
}
