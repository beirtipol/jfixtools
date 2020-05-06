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

import com.beirtipol.jfixtools.logging.FIXLogService;
import com.beirtipol.jfixtools.logging.dictionary.DictionaryMappingService;
import com.beirtipol.jfixtools.logging.model.FIXLogEntry;
import com.beirtipol.jfixtools.logging.model.FIXLogEntryType;
import com.beirtipol.jfixtools.logging.model.FIXSessionID;
import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.DictionaryService;
import com.beirtipol.jfixtools.ui.util.MessageUtils;
import com.beirtipol.jfixtools.ui.vaadin.components.FIXDictionaryCombo;
import com.beirtipol.jfixtools.ui.vaadin.components.FIXMessageViewer;
import com.beirtipol.jfixtools.ui.vaadin.components.FIXSessionCombo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@UIScope
@org.springframework.stereotype.Component
@Route(value = "logs")
public class FIXLogsView extends VerticalLayout {
    private final DictionaryMappingService  dictionaryMappingService;
    private final DictionaryService         dictionaryService;
    private final FIXLogService             fixLogService;
    private       MessageUtils              messageUtils;
    private       FIXMessageViewer          messageViewer;
    private       FIXDictionaryCombo        dictionaryCombo;
    private final ArrayList<FIXLogEntry>    loadedEntries = new ArrayList<>();
    private final DatePicker                toDate;
    private final DatePicker                fromDate;
    private final Grid<FIXLogEntry>         grid;
    private final ComboBox<FIXLogEntryType> typeCombo;

    @Autowired
    public FIXLogsView(DictionaryMappingService dictionaryMappingService,
                       DictionaryService dictionaryService,
                       FIXLogService fixLogService,
                       FIXRepositoryHelper helper,
                       MessageUtils messageUtils) {
        this.dictionaryMappingService = dictionaryMappingService;
        this.dictionaryService        = dictionaryService;
        this.fixLogService            = fixLogService;
        this.messageUtils             = messageUtils;
        setSizeFull();

        HorizontalLayout toolbar = new HorizontalLayout();
        add(toolbar);
        FIXSessionCombo fixSessionCombo = new FIXSessionCombo(fixLogService);
        fixSessionCombo.setWidth("400px");
        fixSessionCombo.setRequired(true);
        toolbar.add(fixSessionCombo);

        typeCombo = new ComboBox<>();
        typeCombo.setRequired(true);
        typeCombo.setItems(FIXLogEntryType.values());
        typeCombo.setValue(FIXLogEntryType.MESSAGE);
        toolbar.add(typeCombo);

        fromDate = new DatePicker();
        fromDate.setValue(LocalDate.now());
        fromDate.setRequired(true);
        toolbar.add(fromDate);

        toDate = new DatePicker();
        toDate.setValue(LocalDate.now().plusDays(1));
        toDate.setRequired(true);
        toolbar.add(toDate);

        toolbar.add(new Button("Load", e -> updateGrid(fixSessionCombo.getValue())));

        grid = new Grid<>();
        ListDataProvider<FIXLogEntry> dataProvider = new ListDataProvider<>(loadedEntries);

        Grid.Column<FIXLogEntry> timeColumn = grid.addColumn(FIXLogEntry::getTime).setHeader("Time").setWidth("250px").setFlexGrow(0);
        Grid.Column<FIXLogEntry> textColumn = grid.addColumn(FIXLogEntry::getText).setHeader("Text").setFlexGrow(1);

        HeaderRow filterRow = grid.appendHeaderRow();
        TextField timeFilter = new TextField();
        timeFilter.addValueChangeListener(event -> dataProvider.refreshAll());
        timeFilter.setValueChangeMode(ValueChangeMode.EAGER);

        filterRow.getCell(timeColumn).setComponent(timeFilter);
        timeFilter.setSizeFull();
        timeFilter.setPlaceholder("Filter");

        TextField textFilter = new TextField();
        textFilter.addValueChangeListener(event -> dataProvider.refreshAll());
        textFilter.setValueChangeMode(ValueChangeMode.EAGER);

        dataProvider.addFilter(entry -> entry.getTime().toString().contains(timeFilter.getValue()));
        dataProvider.addFilter(entry -> entry.getText().contains(textFilter.getValue()));

        filterRow.getCell(textColumn).setComponent(textFilter);
        textFilter.setSizeFull();
        textFilter.setPlaceholder("Filter");

        grid.setDataProvider(dataProvider);
        grid.setSizeFull();


        grid.setItemDetailsRenderer(new ComponentRenderer<Component, FIXLogEntry>() {
            @Override
            public Component createComponent(FIXLogEntry item) {
                if (typeCombo.getValue() == FIXLogEntryType.MESSAGE) {
                    FIXMessageViewer viewer = new FIXMessageViewer();
                    dictionaryMappingService.dictionaryName(fixSessionCombo.getValue())
                            .ifPresent(name -> {
                                dictionaryService.loadDictionary(name).ifPresent(dd -> {
                                    try {
                                        Message message = messageUtils.parse(dd, item.getText());
                                        viewer.setContent(dd, message, helper);
                                    } catch (InvalidMessage e) {
                                        log.error("Could not parse fix message, e");
                                    }
                                });
                            });
                    viewer.setSizeFull();
                    viewer.setHeight("600px");
                    return viewer;
                }
                return new TextArea(item.getText());
            }
        });

        add(grid);

    }

    private void updateGrid(FIXSessionID sessionID) {
        fixLogService.getFIXLogRepositories().stream()
                .filter(repo -> repo.getSessionID().equals(sessionID))
                .filter(repo -> repo.getLogEntryType() == typeCombo.getValue())
                .findFirst()
                .ifPresent(repo -> {
                    loadedEntries.clear();
                    loadedEntries.addAll(repo.getEntriesBetween(
                            fromDate.getValue().atStartOfDay(),
                            toDate.getValue().atStartOfDay()
                    ));
                    grid.getDataProvider().refreshAll();
                });
    }

}