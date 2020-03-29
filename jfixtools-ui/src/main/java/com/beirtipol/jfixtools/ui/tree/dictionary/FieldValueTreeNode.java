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
import fixrepository.Field;
import fixrepository.PurposeT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FieldValueTreeNode implements IDictionaryTreeNode {
    private List<IDictionaryTreeNode> children = new ArrayList<>();
    private IDictionaryTreeNode       parent;
    private String                    enumValue;
    private String                    description;
    private Optional<Field>           fieldInfo;
    private FIXRepositoryHelper       helper;
    private String                    synopsis;

    public FieldValueTreeNode(IDictionaryTreeNode parent, String enumValue, String description, Optional<Field> fieldInfo, FIXRepositoryHelper helper) {
        this.parent      = parent;
        this.enumValue   = enumValue;
        this.description = description;
        this.fieldInfo   = fieldInfo;
        this.helper      = helper;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public List<IDictionaryTreeNode> getChildren() {
        return children;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public IDictionaryTreeNode getParent() {
        return parent;
    }

    @Override
    public String getHierarchyTag() {
        return getEnumValue();
    }

    @Override
    public String getName() {
        return getEnumValue();
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public String getSynopsis() {
        // TODO Auto-generated method stub
        if (fieldInfo.isPresent()) {
            Optional<String> text = helper.getText("ENUM_" + fieldInfo.get().getId() + "_" + enumValue, PurposeT.SYNOPSIS);
            if (text.isPresent()) {
                return text.get();
            }
        }
        return "";
    }

}
