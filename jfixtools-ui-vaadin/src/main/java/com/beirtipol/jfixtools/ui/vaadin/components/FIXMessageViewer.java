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

package com.beirtipol.jfixtools.ui.vaadin.components;

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import com.beirtipol.jfixtools.ui.tree.message.FIXMessageTreeNode;
import com.beirtipol.jfixtools.ui.tree.message.FIXTreeNode;
import com.beirtipol.jfixtools.ui.tree.message.StringFieldTreeNode;
import com.beirtipol.jfixtools.ui.tree.message.XMLTreeNode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import quickfix.Message;

import java.util.stream.Stream;

@UIScope
@Slf4j
public class FIXMessageViewer extends VerticalLayout {
    private static final long                  serialVersionUID = -8111127332166543055L;
    private              TreeGrid<FIXTreeNode> tree;
    private              Message               message;
    private              NamedDataDictionary   dictionary;

    public FIXMessageViewer(FIXRepositoryHelper helper) {
        setMargin(false);
        tree = new TreeGrid<>();
        tree.addHierarchyColumn(FIXTreeNode::getTag).setHeader("Tag").setResizable(true);
        tree.addColumn(FIXTreeNode::getName).setHeader("Name").setResizable(true);
        tree.addColumn(FIXTreeNode::getValue).setHeader("Value").setFlexGrow(1).setResizable(true);
        tree.addColumn(FIXTreeNode::getDescription).setHeader("Description").setFlexGrow(1).setResizable(true);
        tree.setItemDetailsRenderer(new ComponentRenderer<VerticalLayout, FIXTreeNode>() {
            @Override
            public VerticalLayout createComponent(FIXTreeNode item) {
                if (item instanceof StringFieldTreeNode) {
                    return new FIXDataViewer(((StringFieldTreeNode) item).getFieldData(helper));
                } else if (item instanceof XMLTreeNode) {
                    VerticalLayout layout = new VerticalLayout();
                    String xmlString = ((XMLTreeNode) item).getXMLString();
                    TextArea ta = new TextArea();
                    ta.setReadOnly(true);
                    ta.setValue(xmlString);
                    ta.setSizeFull();
                    layout.setHeight("200px");
                    layout.setMargin(false);
                    layout.add(ta);
                    return layout;
                }
                return new VerticalLayout();
            }
        });
        add(tree);
        tree.setSizeFull();
    }

    public void setContent(NamedDataDictionary dictionary, Message message) {
        this.message    = message;
        this.dictionary = dictionary;
        FIXMessageTreeNode rootNode = new FIXMessageTreeNode(this.dictionary, this.message);

        tree.setDataProvider(new AbstractBackEndHierarchicalDataProvider<FIXTreeNode, Object>() {

            private static final long serialVersionUID = -5807324979676746648L;

            @Override
            public int getChildCount(HierarchicalQuery<FIXTreeNode, Object> query) {
                return (int) fetchChildren(query).count();
            }

            @Override
            public boolean hasChildren(FIXTreeNode item) {
                return item.hasChildren();
            }

            @Override
            protected Stream<FIXTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<FIXTreeNode, Object> query) {
                FIXTreeNode parent = query.getParentOptional().orElse(rootNode);
                return parent.getChildren().stream().map(e -> (FIXTreeNode) e);
            }
        });
        FIXTreeNode messageTag = (FIXTreeNode) rootNode.getChildren().get(1);
        tree.expand(messageTag);
    }

}
