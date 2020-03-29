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
import fixrepository.Enum;
import fixrepository.Field;
import fixrepository.PurposeT;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@EqualsAndHashCode
@Data
@Builder
public class EnumData implements FIXData {
    private String              name;
    private Field               field;
    private NamedDataDictionary dictionary;
    private FIXRepositoryHelper helper;
    private Enum                enumField;

    public String getDescription() {
        return dictionary.getValueName(getTag(), name);
    }

    public int getTag() {
        return field.getId().intValue();
    }

    public String getSynopsis() {
        return getDescription(PurposeT.SYNOPSIS);
    }

    public String getElaboration() {
        return getDescription(PurposeT.ELABORATION);
    }

    public String getAdded() {
        return enumField.getAdded();
    }

    public String getDeprecated() {
        return enumField.getDeprecated();
    }

    private String getDescription(PurposeT purpose) {
        String result = "";
        // Strangely, the Enum object has an ElaborationTextID but it doesn't not seem to match anything
        Optional<String> text = helper.getText(enumField.getTextId(), purpose);
        if (text.isPresent()) {
            result = text.get();
        }
        return result;
    }
}
