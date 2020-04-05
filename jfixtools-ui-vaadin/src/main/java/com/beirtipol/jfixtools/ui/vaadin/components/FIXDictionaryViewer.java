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

import com.beirtipol.jfixtools.ui.tree.dictionary.HasFIXData;
import com.beirtipol.jfixtools.ui.tree.dictionary.IDictionaryTreeNode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.stream.Stream;

public class FIXDictionaryViewer extends VerticalLayout {
    private TreeGrid<IDictionaryTreeNode> tree;

    public FIXDictionaryViewer() {
        setMargin(false);
        tree = new TreeGrid<>();
        tree.addHierarchyColumn(IDictionaryTreeNode::getHierarchyTag).setHeader("Tag").setResizable(true);
        tree.addColumn(IDictionaryTreeNode::getName).setHeader("Name").setResizable(true);
        tree.addColumn(IDictionaryTreeNode::isRequired).setHeader("Required").setResizable(true);
        tree.addColumn(IDictionaryTreeNode::getDescription).setHeader("Description").setResizable(true);
        tree.addColumn(IDictionaryTreeNode::getSynopsis).setHeader("Synopsis").setFlexGrow(10).setResizable(true);
        tree.setItemDetailsRenderer(new ComponentRenderer<Component,IDictionaryTreeNode>(){
            @Override
            public Component createComponent(IDictionaryTreeNode item) {
                if (item instanceof HasFIXData) {
                    return new FIXDataViewer(((HasFIXData) item).getFIXData());
                }
                VerticalLayout verticalLayout = new VerticalLayout();
                verticalLayout.setVisible(false);
                return verticalLayout;
            }
        });
        tree.setPageSize(1000);
        add(tree);
        tree.setSizeFull();
        tree.addItemDoubleClickListener(event -> {
            Dialog d = new Dialog(new Label(event.getItem().getSynopsis()));
            d.open();
        });
    }

    public void setContent(IDictionaryTreeNode rootNode) {
        if (rootNode != null) {
            tree.setDataProvider(new AbstractBackEndHierarchicalDataProvider<IDictionaryTreeNode, Object>() {

                private static final long serialVersionUID = -5807324979676746648L;

                @Override
                public int getChildCount(HierarchicalQuery<IDictionaryTreeNode, Object> query) {
                    return (int) fetchChildren(query).count();
                }

                @Override
                public boolean hasChildren(IDictionaryTreeNode item) {
                    return item.hasChildren();
                }

                @Override
                protected Stream<IDictionaryTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<IDictionaryTreeNode, Object> query) {
                    IDictionaryTreeNode parent = query.getParentOptional().orElse(rootNode);
                    return parent.getChildren().stream();
                }
            });
            tree.getDataProvider().refreshAll();
        }
    }
}
