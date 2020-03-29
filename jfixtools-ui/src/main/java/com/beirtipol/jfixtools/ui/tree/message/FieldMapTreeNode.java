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

package com.beirtipol.jfixtools.ui.tree.message;

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import lombok.extern.slf4j.Slf4j;
import quickfix.*;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class FieldMapTreeNode extends FIXTreeNode {
    private String              messageType;
    private FieldMap            fieldMap;
    private FIXRepositoryHelper helper;
    private String              tag;
    private String              name;
    private String              description;
    private List<FIXTreeNode>   children;

    public FieldMapTreeNode(FIXTreeNode parent, NamedDataDictionary dictionary, String messageType, FieldMap fieldMap, FIXRepositoryHelper helper, String tag, String name, String description) {
        super(parent, dictionary);
        this.messageType = messageType;
        this.fieldMap    = fieldMap;
        this.helper      = helper;
        this.tag         = tag;
        this.name        = name;
        this.description = description;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return String.format("size[%s]", getChildren().size());
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<FIXTreeNode> getChildren() {
        if (children == null) {
            Stream<Field<?>> fieldMapStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(fieldMap.iterator(), Spliterator.ORDERED), false);
            children = fieldMapStream.map(f -> {
                StringField field = (StringField) f;
                int fieldNum = field.getField();
                String fieldName = getName(fieldNum);
                String fieldDescription = getDescription(field);
                if (fieldMap.hasGroup(fieldNum)) {
                    IntField integerField = new IntField(fieldNum);
                    if (fieldMap.isSetField(integerField)) {
                        for (int count = 1; count <= integerField.getValue(); count++) {
                            try {
                                Group group = fieldMap.getGroup(count, fieldNum);
                                return new FieldMapTreeNode(this, dictionary, messageType, group, helper, "" + fieldNum, fieldName, fieldDescription);
                            } catch (FieldNotFound e) {
                                log.error("Could not get expected group", e);
                            }
                        }
                    }
                } else {
                    return new StringFieldTreeNode(this, dictionary, helper, messageType, fieldNum, fieldName, field.getValue(), fieldDescription);
                }
                return null;
            }).filter(e -> e != null).collect(Collectors.toList());
        }
        return children;
    }

    @Override
    public boolean hasChildren() {
        return getChildren().size() > 0;
    }

}
