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

package com.beirtipol.jfixtools.ui.tree.dictionary;

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import fixrepository.Field;
import fixrepository.PurposeT;
import quickfix.DataDictionary;
import quickfix.DataDictionary.GroupInfo;
import quickfix.FieldType;

import java.util.*;
import java.util.stream.Collectors;

public class FieldTreeNode implements IDictionaryTreeNode {

    private NamedDataDictionary       dict;
    private List<IDictionaryTreeNode> children;
    private int                       field;
    private IDictionaryTreeNode       parent;
    private String                    messageType;
    private FIXRepositoryHelper       helper;
    private Optional<Field>           fieldInfo;
    private String                    synopsis;

    public FieldTreeNode(IDictionaryTreeNode parent, int field, NamedDataDictionary dict, String messageType, FIXRepositoryHelper helper) {
        this.parent      = parent;
        this.field       = field;
        this.dict        = dict;
        this.messageType = messageType;
        this.helper      = helper;
        this.fieldInfo   = helper.loadFieldInfo(field);
    }

    @Override
    public List<IDictionaryTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();

            if (isGroup()) {
                GroupInfo groupInfo = dict.getGroup(messageType, field);
                DataDictionary groupDD = groupInfo.getDataDictionary();
                children.addAll(Arrays.stream(groupDD.getOrderedFields())//
                        .mapToObj(childField -> new FieldTreeNode(this, //
                                childField, //
                                new NamedDataDictionary(dict.getFieldName(field), dict), messageType, helper))//
                        .collect(Collectors.toList()));

            } else {
                Set<String> fieldValues = dict.getFieldValues(field);
                children.addAll(fieldValues.stream()//
                        .map(value -> new FieldValueTreeNode(this, value, dict.getValueName(field, value), fieldInfo, helper))//
                        .collect(Collectors.toList()));
            }
        }
        return children;
    }

    public int getField() {
        return field;
    }

    public String getName() {
        return dict.getFieldName(field);
    }

    public String getType() {
        FieldType fieldType = dict.getFieldType(field);
        if (fieldType != null) {
            return fieldType.name();
        }
        return "";
    }

    public boolean isRequired() {
        return dict.isRequiredField(messageType, field);
    }

    @Override
    public String getHierarchyTag() {
        return "" + getField();
    }

    @Override
    public String getDescription() {
        if (isGroup()) {
            return "Group Field";
        } else if (hasChildren()) {
            return "Enum : " + getJavaType();
        }
        return getType() + " : " + getJavaType();
    }

    public String getJavaType() {
        FieldType fieldType = dict.getFieldType(field);
        if (fieldType != null) {
            Class<?> javaType = dict.getFieldType(field).getJavaType();
            if (javaType != null) {
                return javaType.getSimpleName();
            }
        }
        return "";
    }

    public boolean isGroup() {
        return dict.isGroup(messageType, field);
    }

    @Override
    public boolean hasChildren() {
        return getChildren().size() > 0;
    }

    @Override
    public IDictionaryTreeNode getParent() {
        return parent;
    }

    @Override
    public String getSynopsis() {
        if (synopsis == null) {
            synopsis = "";
            if (fieldInfo.isPresent()) {
                Optional<String> synopsisText = helper.getText(fieldInfo.get().getTextId(), PurposeT.SYNOPSIS);
                if (synopsisText.isPresent()) {
                    synopsis = synopsisText.get();
                }
            }
        }
        return synopsis;
    }
}
