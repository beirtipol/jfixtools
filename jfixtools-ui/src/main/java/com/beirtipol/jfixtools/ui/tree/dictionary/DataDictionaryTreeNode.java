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

import java.util.*;
import java.util.stream.Collectors;

public class DataDictionaryTreeNode implements IDictionaryTreeNode {
    private List<IDictionaryTreeNode> children;
    private NamedDataDictionary       dict;
    private FIXRepositoryHelper       helper;

    public DataDictionaryTreeNode(NamedDataDictionary dict, FIXRepositoryHelper helper) {
        this.dict   = dict;
        this.helper = helper;
    }

    @Override
    public List<IDictionaryTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            if (dict != null) {
                Map<String, Set<Integer>> messageFields = dict.getMessageFields();

                children.addAll(
                        messageFields.keySet()
                                .stream()
                                .map(msgType -> new MessageTypeTreeNode(this, msgType, dict, helper))
                                .sorted(Comparator.comparing(MessageTypeTreeNode::getName))
                                .collect(Collectors.toList()));
            }
        }
        return children;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public IDictionaryTreeNode getParent() {
        return null;
    }

    @Override
    public String getHierarchyTag() {
        return dict.getName();
    }

    @Override
    public String getName() {
        return getName();
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public String getDescription() {
        return dict.getVersion();
    }

    @Override
    public String getSynopsis() {
        return "";
    }

}
