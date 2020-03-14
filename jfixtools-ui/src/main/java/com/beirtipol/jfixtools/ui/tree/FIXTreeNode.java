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
import quickfix.StringField;

public abstract class FIXTreeNode implements ITreeNode<FIXTreeNode> {

    private FIXTreeNode parent;
    protected DataDictionary dictionary;

    public FIXTreeNode(FIXTreeNode parent, DataDictionary dictionary) {
        this.parent = parent;
        this.dictionary = dictionary;
    }

    protected String getName(int field) {
        return dictionary.getFieldName(field);
    }

    protected String getDescription(StringField field) {
        if (dictionary.hasFieldValue(field.getField())) {
            return dictionary.getValueName(field.getField(), field.getValue());
        }
        return "";
    }

    public FIXTreeNode getParent() {
        return parent;
    }

    public abstract String getTag();

    public abstract String getName();

    public abstract String getValue();

    public abstract String getDescription();
}
