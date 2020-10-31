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

package com.beirtipol.jfixtools.web.controller;

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.DictionaryService;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import com.beirtipol.jfixtools.ui.field.FieldData;
import com.beirtipol.jfixtools.ui.field.MessageData;
import com.beirtipol.jfixtools.ui.tree.dictionary.DataDictionaryTreeNode;
import com.beirtipol.jfixtools.web.dto.HierarchicalData;
import fixrepository.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
public class RepositoryController {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private FIXRepositoryHelper helper;


    @GetMapping("/repository/{dictionaryName}")
    public Object dictionaryNode(@PathVariable("dictionaryName") String dictionaryName,
                                 @RequestParam("messageType") Optional<String> messageType,
                                 @RequestParam("fieldName") Optional<String> fieldName,
                                 @RequestParam("fieldNumber") Optional<Integer> fieldNumber,
                                 @RequestParam("parentId") Optional<Integer> parentId) {

        Optional<NamedDataDictionary> namedDataDictionary = dictionaryService.loadDictionary(dictionaryName);
        if (namedDataDictionary.isPresent()) {
            if (fieldName.isPresent() || fieldNumber.isPresent() || parentId.isPresent()) {
                FieldData.FieldDataBuilder fieldBuilder = FieldData.builder();
                fieldBuilder.helper(helper);
                int tagNum;
                Optional<Field> field = Optional.empty();
                if (parentId.isPresent()) {
                    tagNum = parentId.get();
                } else if (fieldName.isPresent()) {
                    field  = helper.loadFieldInfo(fieldName.get());
                    tagNum = field.get().getId().intValue();
                } else if (fieldNumber.isPresent()) {
                    tagNum = fieldNumber.get();
                } else {
                    throw new IllegalArgumentException("Either {fieldNumber} or {fieldName} is required");
                }
                // Support either Int or String field

                fieldBuilder.tagNum(tagNum);
                fieldBuilder.dictionary(namedDataDictionary.get());
                messageType.ifPresent(mt -> fieldBuilder.messageType(mt));
                FieldData fieldData = fieldBuilder.build();
                return HierarchicalData.builder().data(Arrays.asList(fieldData)).build();
            } else if (messageType.isPresent()) {
                // Return all fields for a message
                MessageData messageData = MessageData.builder()
                        .dictionary(namedDataDictionary.get())
                        .helper(helper)
                        .messageType(messageType.get())
                        .build();
                return HierarchicalData.builder().data(Arrays.asList(messageData)).build();
            } else {
                DataDictionaryTreeNode dataDictionaryTreeNode = new DataDictionaryTreeNode(namedDataDictionary.get(), helper);

                return HierarchicalData.builder().data(Arrays.asList(dataDictionaryTreeNode)).build();
            }
        } else {
            throw new IllegalArgumentException("Unknown dictionary: " + dictionaryName);
        }
    }

}
