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

package com.beirtipol.jfixtools.ui.tree;

import quickfix.DataDictionary;

import java.util.ArrayList;
import java.util.List;

public class StringFieldTreeNode extends FIXTreeNode {
    private int tag;
    private String value;
    private String description;
    private String fieldName;
    private ArrayList<FIXTreeNode> children = new ArrayList<>();

    public StringFieldTreeNode(FIXTreeNode parent, DataDictionary dictionary, int tag, String fieldName, String value, String description) {
        super(parent, dictionary);
        this.tag = tag;
        this.fieldName = fieldName;
        this.value = value;
        this.description = description;
    }

    @Override
    public String getTag() {
        return "" + tag;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<FIXTreeNode> getChildren() {
        return children;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }
}
