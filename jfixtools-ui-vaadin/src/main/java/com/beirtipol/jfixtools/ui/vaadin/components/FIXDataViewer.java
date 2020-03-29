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

import com.beirtipol.jfixtools.ui.field.FIXData;
import com.beirtipol.jfixtools.ui.field.FieldData;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.TextRenderer;

import java.util.ArrayList;
import java.util.List;

public class FIXDataViewer extends VerticalLayout {
    public FIXDataViewer(FIXData field) {
        Grid<FIXData> fieldGrid = createGrid();
        fieldGrid.setItems(field);
        fieldGrid.setMinHeight("200px");
        setMinHeight("200px");
        add(fieldGrid);


        if (field instanceof FieldData) {
            List<FIXData> data = new ArrayList<>();
            data.addAll(((FieldData) field).getEnums());
            if (data.size() > 0) {
                add(new Label("Possible Values:"));
                Grid<FIXData> enumGrid = createGrid();
                enumGrid.setItems(data);
                enumGrid.setMinHeight("400px");
                setMinHeight("400px");
                add(enumGrid);
            }
        }

        setWidth("100%");

        setMargin(false);
    }

    public Grid<FIXData> createGrid() {
        Grid<FIXData> fieldGrid = new Grid<>();
        fieldGrid.addColumn(FIXData::getTag).setHeader("Id").setWidth("50px").setResizable(true);
        fieldGrid.addColumn(FIXData::getName).setHeader("Name").setWidth("100px").setResizable(true);
        fieldGrid.addColumn(FIXData::getSynopsis).setHeader("Description").setWidth("300px").setResizable(true);
        fieldGrid.addColumn(FIXData::getAdded).setHeader("Added").setWidth("100px").setResizable(true);
        fieldGrid.addColumn(FIXData::getDeprecated).setHeader("Deprecated").setWidth("100px").setResizable(true);
        fieldGrid.setSizeFull();

        fieldGrid.setItemDetailsRenderer(new TextRenderer<>() {
            @Override
            public Component createComponent(FIXData fixData) {
                TextArea result = new TextArea();
                StringBuilder sb = new StringBuilder();
                sb.append(fixData.getSynopsis());
                sb.append("\n\n");
                sb.append(fixData.getElaboration());
                result.setValue(sb.toString());
                result.setSizeFull();
                result.setReadOnly(true);
                return result;
            }
        });

        return fieldGrid;
    }
}
