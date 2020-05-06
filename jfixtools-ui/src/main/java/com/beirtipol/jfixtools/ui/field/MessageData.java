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
import fixrepository.Message;
import fixrepository.PurposeT;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import quickfix.DataDictionary;
import quickfix.field.MsgType;

import java.util.Optional;

/**
 * This class serves as a decorator for a FIXRepository field. Its purpose is to provide easy access to the data contained
 * within the repository. It standardises the methods available on Fields and Enums to make it easily usable within
 * a UI grid.
 */
@EqualsAndHashCode
@Data
public class MessageData implements FIXData {
    private String              messageType;
    private NamedDataDictionary dictionary;
    private FIXRepositoryHelper helper;

    private Optional<Message> message;

    @Builder
    public MessageData(String messageType, NamedDataDictionary dictionary, FIXRepositoryHelper helper) {
        this.messageType = messageType;
        this.dictionary  = dictionary;
        this.helper      = helper;
    }

    public Optional<Message> getMessage() {
        if (message == null) {
            message = helper.loadMessageInfo(messageType);
        }
        return message;
    }

    @Override
    public String getTag() {
        return messageType;
    }

    public String getName() {
        if (DataDictionary.HEADER_ID.equals(messageType)) {
            return "Header";
        } else if (DataDictionary.TRAILER_ID.equals(messageType)) {
            return "Trailer";
        }
        return dictionary.getValueName(MsgType.FIELD, messageType);
    }

    public String getAdded() {
        if (getMessage().isPresent()) {
            return getMessage().get().getAdded();
        }
        return "";
    }

    public String getDeprecated() {
        if (getMessage().isPresent()) {
            return getMessage().get().getDeprecated();
        }
        return "";
    }

    public String getType() {
        return messageType;
    }

    public String getSynopsis() {
        return getDescription(PurposeT.SYNOPSIS);
    }

    public String getElaboration() {
        return getDescription(PurposeT.ELABORATION);
    }

    private String getDescription(PurposeT purpose) {
        String result = "";
        if (getMessage().isPresent()) {
            Optional<String> text = helper.getText(getMessage().get().getTextId(), purpose);
            if (text.isPresent()) {
                result = text.get();
            }
        }
        return result;
    }
}
