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
import com.beirtipol.jfixtools.ui.field.FieldData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.List;

@JsonComponent
public class FieldDataSerializer extends JsonSerializer<FieldData> {
    @Override
    public void serialize(FieldData fieldData, JsonGenerator jg, SerializerProvider serializerProvider) throws IOException {
        jg.writeStartObject();
        jg.writeStringField("ID", fieldData.getTag());
        jg.writeStringField("name", fieldData.getName());
        jg.writeBooleanField("required", fieldData.required());
        jg.writeStringField("type", fieldData.getType());
        jg.writeStringField("synopsis", fieldData.getSynopsis());
        jg.writeStringField("elaboration", fieldData.getElaboration());
        jg.writeStringField("added", fieldData.getAdded());
        jg.writeStringField("deprecated", fieldData.getDeprecated());
        List<EnumData> enums = fieldData.getEnums();
        if (enums.size() > 0) {
            jg.writeObjectField("items", enums);
        }
        jg.writeEndObject();
    }
}
