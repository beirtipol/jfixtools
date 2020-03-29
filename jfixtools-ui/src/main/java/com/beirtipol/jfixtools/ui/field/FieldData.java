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
import quickfix.FieldType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Data
@Builder
public class FieldData implements FIXData {
    private Optional<Field>     field;
    private int                 tag;
    private NamedDataDictionary dictionary;
    private FIXRepositoryHelper helper;
    private String              messageType;
    private List<EnumData>      enums;

    public Optional<Field> getField() {
        if (field == null) {
            field = helper.loadFieldInfo(tag);
        }
        return field;
    }

    public String getName() {
        if (getField().isPresent()) {
            return getField().get().getName();
        }
        return "";
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
            return dictionary.isGroup(messageType, tag);
        }
        return false;
    }

    public String getType() {
        FieldType fieldType = dictionary.getFieldType(tag);
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
            return dictionary.isRequiredField(messageType, tag);
        }
        return false;
    }

    private String getDescription(PurposeT purpose) {
        String result = "";
        if (field.isPresent()) {
            Optional<String> text = helper.getText(getField().get().getTextId(), purpose);
            if (text.isPresent()) {
                result = text.get();
            }
        }
        return result;
    }

    public Class<?> getJavaType() {
        FieldType fieldType = dictionary.getFieldType(tag);
        if (fieldType != null) {
            return dictionary.getFieldType(tag).getJavaType();
        }
        return Class.class;
    }
}
