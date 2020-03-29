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

import com.beirtipol.jfixtools.ui.dictionary.DictionaryService;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import com.vaadin.flow.component.combobox.ComboBox;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FIXDictionaryCombo extends ComboBox<NamedDataDictionary> {

    private static final long                      serialVersionUID = 8708812196501035789L;
    private final        List<NamedDataDictionary> dictionaries;

    public FIXDictionaryCombo(DictionaryService dictionaryService) {
        dictionaries = dictionaryService.loadAll().collect(Collectors.toList());
        setItems(dictionaries);
        setItemLabelGenerator(item -> item.getName());
    }

    public void choose(String dictionaryName) {
        dictionaries.stream().filter(d -> dictionaryName.equals(d.getName())).findFirst().ifPresent(dd -> setValue(dd));
    }
}