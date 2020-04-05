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

package com.beirtipol.jfixtools.ui.field;

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import fixrepository.Field;
import fixrepository.PurposeT;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import quickfix.FieldType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class serves as a decorator for a FIXRepository field. Its purpose is to provide easy access to the data contained
 * within the repository. It standardises the methods available on Fields and Enums to make it easily usable within
 * a UI grid.
 */
@EqualsAndHashCode
@Data
@Builder
public class FieldData implements FIXData {
    private final int                 tagNum;
    private final NamedDataDictionary dictionary;
    private final FIXRepositoryHelper helper;
    private String              messageType;

    private List<EnumData>  enums;
    private Optional<Field> field;

    public Optional<Field> getField() {
        if (field == null) {
            field = helper.loadFieldInfo(tagNum);
        }
        return field;
    }

    public String getTag() {
        return "" + tagNum;
    }

    public String getName() {
        if (getField().isPresent()) {
            return getField().get().getName();
        }
        return "";
    }

    public boolean isRequired() {
        if (messageType != null) {
            return dictionary.isRequiredField(messageType, tagNum);
        }
        return false;
    }

    public boolean isEnum() {
        return getEnums().size() > 0;
    }

    public String getAdded() {
        if (getField().isPresent()) {
            return getField().get().getAdded();
        }
        return "";
    }

    public String getDeprecated() {
        if (getField().isPresent()) {
            return getField().get().getDeprecated();
        }
        return "";
    }

    public List<EnumData> getEnums() {
        if (enums == null && getField().isPresent()) {
            enums = getField().get().getEnum().stream()
                    .map(e -> EnumData.builder()
                            .dictionary(dictionary)
                            .helper(helper)
                            .field(getField().get())
                            .name(e.getValue())
                            .enumField(e)
                            .build()).collect(Collectors.toList());
        }
        return enums;
    }

    public boolean isGroup() {
        if (messageType != null) {
            return dictionary.isGroup(messageType, tagNum);
        }
        return false;
    }

    public String getType() {
        FieldType fieldType = dictionary.getFieldType(tagNum);
        if (fieldType != null) {
            return fieldType.name();
        }
        return "";
    }

    public String getSynopsis() {
        return getDescription(PurposeT.SYNOPSIS);
    }

    public String getElaboration() {
        return getDescription(PurposeT.ELABORATION);
    }

    public boolean required() {
        if (messageType != null) {
            return dictionary.isRequiredField(messageType, tagNum);
        }
        return false;
    }

    private String getDescription(PurposeT purpose) {
        String result = "";
        if (getField().isPresent()) {
            Optional<String> text = helper.getText(getField().get().getTextId(), purpose);
            if (text.isPresent()) {
                result = text.get();
            }
        }
        return result;
    }

    public Class<?> getJavaType() {
        FieldType fieldType = dictionary.getFieldType(tagNum);
        if (fieldType != null) {
            return dictionary.getFieldType(tagNum).getJavaType();
        }
        return Class.class;
    }
}
