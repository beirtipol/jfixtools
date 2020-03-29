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
import com.beirtipol.jfixtools.ui.tree.ITreeNode;
import fixrepository.Message;
import fixrepository.PurposeT;
import quickfix.DataDictionary;
import quickfix.field.MsgType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageTypeTreeNode implements IDictionaryTreeNode {

    private NamedDataDictionary       dict;
    private List<IDictionaryTreeNode> children;
    private ITreeNode<?>              parent;
    private String                    messageType;
    private FIXRepositoryHelper       helper;
    private Optional<Message>         messageInfo;
    private String                    synopsis;

    public MessageTypeTreeNode(ITreeNode<?> parent, String messageType, NamedDataDictionary dict, FIXRepositoryHelper helper) {
        this.parent = parent;
        this.messageType = messageType;
        this.dict = dict;
        this.helper = helper;
        this.messageInfo = helper.loadMessageInfo(messageType);
    }

    @Override
    public List<IDictionaryTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            children.addAll(dict.getMessageFields(messageType).stream()//
                    .map(field -> new FieldTreeNode(this, field, dict, messageType, helper))//
                    .collect(Collectors.toList()));
        }
        return children;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getName() {
        if (DataDictionary.HEADER_ID.equals(messageType)) {
            return "Header";
        } else if (DataDictionary.TRAILER_ID.equals(messageType)) {
            return "Trailer";
        }
        return dict.getValueName(MsgType.FIELD, messageType);
    }

    @Override
    public boolean hasChildren() {
        return getChildren().size() > 0;
    }

    @Override
    public ITreeNode<?> getParent() {
        return parent;
    }

    @Override
    public String getHierarchyTag() {
        return getName();
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public String getDescription() {
        return getMessageType() + " : " + getName();
    }

    @Override
    public String getSynopsis() {
        if (synopsis == null) {
            synopsis = "";
            if (messageInfo.isPresent()) {
                Optional<String> synopsisText = helper.getText(messageInfo.get().getTextId(), PurposeT.SYNOPSIS);
                if (synopsisText.isPresent()) {
                    synopsis = synopsisText.get();
                }
            }
        }
        return synopsis;
    }
}