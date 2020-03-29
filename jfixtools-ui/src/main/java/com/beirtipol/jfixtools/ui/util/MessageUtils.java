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

package com.beirtipol.jfixtools.ui.util;

import quickfix.DataDictionary;
import quickfix.DefaultMessageFactory;
import quickfix.InvalidMessage;
import quickfix.Message;

public class MessageUtils {
    /**
     * Parse a message using the specified dictionary. This simply avoids having to specify a MessageFactory to
     * QuickFixJ's MessageUtils
     *
     * @param dict
     * @param text
     * @return
     * @throws InvalidMessage is rethrown from the quickfixj MessageUtils
     */
    public static Message parse(DataDictionary dict, String text) throws InvalidMessage {
        return quickfix.MessageUtils.parse(new DefaultMessageFactory(), dict, text);
    }

    /**
     * Performs a checksum calculation and substitution using raw text replace. This helps avoid any Dictionary
     * strangeness and ensures that if the message is at least reasonably valid, we can correct the checksum
     *
     * @param text
     * @return
     */
    public static String correctChecksum(String text) {
        int checksum = quickfix.MessageUtils.checksum(text);
        return text.replaceAll("10=\\d+", "10=" + checksum + "");
    }

    /**
     * FIX messages are often communicated over email using pipes as delimiters as certain systems cannot generate
     * the \u0001 (SOH) character
     *
     * @param text
     * @return
     */
    public static String replacePipeWithSOH(String text) {
        return correctChecksum(text.replaceAll("\\|", ""));
    }
}
