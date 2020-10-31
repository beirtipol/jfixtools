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

import com.beirtipol.jfixtools.ui.tree.dictionary.IDictionaryTreeNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class IDictionaryTreeNodeSerializer extends JsonSerializer<IDictionaryTreeNode> {
    @Override
    public void serialize(IDictionaryTreeNode node, JsonGenerator jg, SerializerProvider serializerProvider) throws IOException {
        jg.writeStartObject();
        jg.writeStringField("ID", node.getHierarchyTag());
        jg.writeStringField("name", node.getName());
        jg.writeBooleanField("required", node.isRequired());
        jg.writeStringField("description", node.getDescription());
        jg.writeStringField("synopsis", node.getSynopsis());
        jg.writeStringField("elaboration", node.getElaboration());
        jg.writeStringField("added", node.getAdded());
        jg.writeStringField("deprecated", node.getDeprecated());
        jg.writeBooleanField("hasItems", node.hasChildren());
        
        jg.writeEndObject();
    }
}
