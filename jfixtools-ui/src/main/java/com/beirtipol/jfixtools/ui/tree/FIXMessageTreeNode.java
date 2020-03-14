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
import quickfix.Message;

import java.util.ArrayList;
import java.util.List;

public class FIXMessageTreeNode extends FIXTreeNode {
    private Message message;
    private List<FIXTreeNode> children;

    public FIXMessageTreeNode(DataDictionary dictionary, Message message) {
        super(null, dictionary);
        this.message = message;
    }

    @Override
    public String getTag() {
        return "Message";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public List<FIXTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            children.add(new FieldMapTreeNode(this, dictionary, message.getHeader(), "Header", "", ""));
            children.add(new FieldMapTreeNode(this, dictionary, message, "Message", "", ""));
            children.add(new FieldMapTreeNode(this, dictionary, message.getTrailer(), "Trailer", "", ""));
        }
        return children;
    }

    @Override
    public boolean hasChildren() {
        return children.size() > 0;
    }

    @Override
    public FIXTreeNode getParent() {
        return null;
    }

}
