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

package com.beirtipol.jfixtools.ui.vaadin.routes;

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.DictionaryService;
import com.beirtipol.jfixtools.ui.field.FieldData;
import com.beirtipol.jfixtools.ui.vaadin.components.FIXDataViewer;
import com.beirtipol.jfixtools.ui.vaadin.components.FIXDictionaryCombo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import fixrepository.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@UIScope
@Component
@Route(value = "repository")
public class FIXRepositoryView extends VerticalLayout {

    private final TextField                           searchText;
    private final ComboBox<SearchType>                searchTypeCombo;
    private       FIXRepositoryHelper                 helper;
    private       DictionaryService                   dictionaryService;
    private final FIXDictionaryCombo                  ddCombo;
    private       com.vaadin.flow.component.Component previous;

    private enum SearchType {
        FieldTag("Field by tag number"),
        FieldName("Field by Name"),
        MessageType("Message Type");

        private String label;

        private SearchType(String label) {
            this.label = label;
        }
    }

    @Autowired
    public FIXRepositoryView(FIXRepositoryHelper helper,
                             DictionaryService dictionaryService) {
        this.helper            = helper;
        this.dictionaryService = dictionaryService;
        setSizeFull();

        HorizontalLayout searchBar = new HorizontalLayout();
        add(searchBar);

        ddCombo = new FIXDictionaryCombo(dictionaryService);
        searchBar.add(ddCombo);

        searchText = new TextField();
        searchBar.add(searchText);

        searchTypeCombo = new ComboBox<>();
        searchTypeCombo.setItems(SearchType.values());
        searchTypeCombo.setValue(SearchType.FieldTag);
        searchBar.add(searchTypeCombo);

        Button searchButton = new Button("Search", e -> search());
        searchBar.add(searchButton);

        previous = new VerticalLayout();
        add(previous);
    }

    private void search() {
        switch (searchTypeCombo.getValue()) {
            case FieldTag:
                Optional<Field> fieldInfo = helper.loadFieldInfo(Integer.parseInt(searchText.getValue()));
                Optional<FieldData> fieldData = fieldInfo.map(fi -> FieldData.builder()
                        .helper(helper)
                        .dictionary(ddCombo.getValue())
                        .tag(fi.getId().intValue())
                        .build());
                fieldData.ifPresent(fd -> {
                    FIXDataViewer viewer = new FIXDataViewer(fd);
                    viewer.setSizeFull();
                    replace(previous, viewer);
                    previous = viewer;
                });
                break;

        }
    }
}
