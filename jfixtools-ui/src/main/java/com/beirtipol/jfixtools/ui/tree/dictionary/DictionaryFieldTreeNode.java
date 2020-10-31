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
import com.beirtipol.jfixtools.ui.field.FIXData;
import com.beirtipol.jfixtools.ui.field.FieldData;
import fixrepository.Field;
import quickfix.DataDictionary;
import quickfix.DataDictionary.GroupInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This provides information for a {@link DataDictionary} {@link Field} rather than a {@link quickfix.Message}
 * {@link quickfix.Field}
 */
public class DictionaryFieldTreeNode implements IDictionaryTreeNode, HasFIXData {

    private       NamedDataDictionary       dict;
    private       List<IDictionaryTreeNode> children;
    private       int                       field;
    private       IDictionaryTreeNode       parent;
    private       String                    messageType;
    private       FIXRepositoryHelper       helper;
    private       String                    synopsis;
    private final FieldData                 fieldData;

    public DictionaryFieldTreeNode(IDictionaryTreeNode parent, int field, NamedDataDictionary dict, String messageType, FIXRepositoryHelper helper) {
        this.parent      = parent;
        this.field       = field;
        this.dict        = dict;
        this.messageType = messageType;
        this.helper      = helper;
        fieldData        = FieldData.builder()
                .tagNum(field)
                .dictionary(dict)
                .messageType(messageType)
                .helper(helper)
                .build();
    }

    public FIXData getFIXData() {
        return fieldData;
    }

    @Override
    public List<IDictionaryTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();

            if (fieldData.isGroup()) {
                GroupInfo groupInfo = dict.getGroup(messageType, field);
                DataDictionary groupDD = groupInfo.getDataDictionary();
                children.addAll(Arrays.stream(groupDD.getOrderedFields())//
                        .mapToObj(childField -> new DictionaryFieldTreeNode(this, //
                                childField, //
                                new NamedDataDictionary(dict.getFieldName(field), dict), messageType, helper))//
                        .collect(Collectors.toList()));

            }
        }
        return children;
    }

    public int getField() {
        return field;
    }

    public String getName() {
        return fieldData.getName();
    }

    public String getType() {
        return fieldData.getType();
    }

    public boolean isRequired() {
        return fieldData.isRequired();
    }

    @Override
    public String getHierarchyTag() {
        return "" + getField();
    }

    @Override
    public String getDescription() {
        if (fieldData.isGroup()) {
            return "Group Field";
        } else if (hasChildren()) {
            return "Enum : " + fieldData.getJavaType().getSimpleName();
        }
        return getType() + " : " + fieldData.getJavaType().getSimpleName();
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
            synopsis = fieldData.getSynopsis();
        }
        return synopsis;
    }

    @Override
    public String getElaboration() {
        return fieldData.getElaboration();
    }

    @Override
    public String getAdded() {
        return fieldData.getAdded();
    }

    @Override
    public String getDeprecated() {
        return fieldData.getDeprecated();
    }
}
