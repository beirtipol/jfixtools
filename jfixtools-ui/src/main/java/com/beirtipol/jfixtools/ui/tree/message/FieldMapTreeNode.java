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

import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import quickfix.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Slf4j
public class FieldMapTreeNode extends FIXTreeNode {
    private String            messageType;
    private FieldMap          fieldMap;
    private String            tag;
    private String            name;
    private String            description;
    private List<FIXTreeNode> children;

    public FieldMapTreeNode(FIXTreeNode parent, NamedDataDictionary dictionary, String messageType, FieldMap fieldMap, String tag, String name, String description) {
        super(parent, dictionary);
        this.messageType = messageType;
        this.fieldMap    = fieldMap;
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

    @SneakyThrows
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
                    try {
                        IntField integerField = fieldMap.getField(new IntField(fieldNum));
                        if (fieldMap.isSetField(integerField)) {
                            for (int count = 1; count <= integerField.getValue(); count++) {
                                Group group = fieldMap.getGroup(count, fieldNum);
                                return new FieldMapTreeNode(this, dictionary, messageType, group, "" + fieldNum, fieldName, fieldDescription);
                            }
                        }
                    } catch (FieldNotFound e) {
                        log.error("Could not get expected group", e);
                    }
                } else {
                    return new StringFieldTreeNode(this, dictionary, messageType, fieldNum, fieldName, field.getValue(), fieldDescription);
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

    @Override
    public void addToJSON(Map<String, Object> json) {
        Stream<Field<?>> fieldMapStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(fieldMap.iterator(), Spliterator.ORDERED), false);
        fieldMapStream.forEach(f -> {
            StringField field = (StringField) f;
            int fieldNum = field.getField();
            String fieldName = getName(fieldNum);
            String fieldDescription = getDescription(field);
            if (fieldMap.hasGroup(fieldNum)) {
                try {
                    IntField integerField = fieldMap.getField(new IntField(fieldNum));
                    if (fieldMap.isSetField(integerField)) {
                        List<Object> groups = new ArrayList<>();
                        for (int count = 1; count <= integerField.getValue(); count++) {
                            Group group = fieldMap.getGroup(count, fieldNum);
                            LinkedHashMap<String, Object> groupMap = new LinkedHashMap<>();
                            new FieldMapTreeNode(this, dictionary, messageType, group, "" + fieldNum, fieldName, fieldDescription).addToJSON(groupMap);
                            groups.add(groupMap);
                        }
                        json.put(fieldName, groups);
                    }
                } catch (FieldNotFound e) {
                    log.error("Could not get expected group", e);
                }
            } else {
                addFieldToJSON(json, field, fieldName);
            }
        });
    }

    private void addFieldToJSON(Map<String, Object> json, StringField field, String fieldName) {
        FieldType fieldType = dictionary.getFieldType(field.getField());
        if(fieldType == null){
            // Fields not listed in the dictionary
            fieldType = FieldType.STRING;
        }
        switch (fieldType) {
            case BOOLEAN:
                json.put(fieldName, Boolean.valueOf(field.getValue()));
                break;
            case INT:
            case NUMINGROUP:
            case LENGTH:
            case SEQNUM:
            case DAYOFMONTH:
                json.put(fieldName, Integer.parseInt(field.getValue()));
                break;
            case QTY:
            case AMT:
            case PRICE:
            case PRICEOFFSET:
            case PERCENTAGE:
                json.put(fieldName, Double.parseDouble(field.getValue()));
                break;
            case FLOAT:
                json.put(fieldName, Float.parseFloat(field.getValue()));
                break;
            case UTCTIMESTAMP:
                json.put(fieldName, LocalDateTime.parse(field.getValue(), DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS")));
                break;
            case UTCDATE:
            case UTCDATEONLY:
                json.put(fieldName, LocalDate.parse(field.getValue(), DateTimeFormatter.ofPattern("yyyyMMdd")));
                break;
            case UTCTIMEONLY:
                json.put(fieldName, LocalTime.parse(field.getValue()));
                break;
            default:
                json.put(fieldName, field.getValue());
                break;
        }
    }
}
