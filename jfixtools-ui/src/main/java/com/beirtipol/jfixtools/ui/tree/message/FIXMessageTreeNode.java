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
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FIXMessageTreeNode extends FIXTreeNode {
    private Message             message;
    private List<FIXTreeNode>   children;
    private FIXRepositoryHelper helper;
    private String              messageType;

    public FIXMessageTreeNode(NamedDataDictionary dictionary, Message message, FIXRepositoryHelper helper) {
        super(null, dictionary);
        this.message = message;
        this.helper = helper;
        try {
            messageType = message.getHeader().getString(MsgType.FIELD);
        } catch (FieldNotFound e) {
            log.error("No MsgType in message. Limited data available", e);
            messageType = "";
        }
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
        return messageType + ": " + dictionary.getValueName(MsgType.FIELD, messageType);
    }

    @Override
    public List<FIXTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            children.add(new FieldMapTreeNode(this, dictionary, messageType, message.getHeader(), helper, "Header", "", ""));
            children.add(new FieldMapTreeNode(this, dictionary, messageType, message, helper, "Message", "", ""));
            children.add(new FieldMapTreeNode(this, dictionary, messageType, message.getTrailer(), helper, "Trailer", "", ""));
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
