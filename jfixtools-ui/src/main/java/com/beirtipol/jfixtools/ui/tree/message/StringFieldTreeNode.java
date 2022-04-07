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

package com.beirtipol.jfixtools.ui.tree.message;

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import com.beirtipol.jfixtools.ui.field.FieldData;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import quickfix.field.XmlData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This allows simple display of a field in a FIX {@link quickfix.Message}. It treats all children as plain text but
 * has a special case treatment for the {@link XmlData} field which it returns as a child node.
 *
 * @see XMLTreeNode
 */
@Slf4j
public class StringFieldTreeNode extends FIXTreeNode {
    private String                 messageType;
    private int                    tag;
    private String                 value;
    private String                 description;
    private String                 fieldName;
    private ArrayList<FIXTreeNode> children;

    public StringFieldTreeNode(FIXTreeNode parent, NamedDataDictionary dictionary, String messageType, int tag, String fieldName, String value, String description) {
        super(parent, dictionary);
        this.messageType = messageType;
        this.tag         = tag;
        this.fieldName   = fieldName;
        this.value       = value;
        this.description = description;
    }

    @Override
    public String getTag() {
        return "" + tag;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<FIXTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            if (tag == XmlData.FIELD) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new InputSource(new StringReader(value)));
                    children.add(new XMLTreeNode(document, this, dictionary));
                } catch (ParserConfigurationException e) {
                    log.error("Unable to create DocumentBuilder", e);
                } catch (SAXException | IOException e) {
                    log.error("Unable to parse XML Document", e);
                }
            }
        }
        return children;
    }

    @Override
    public boolean hasChildren() {
        return getChildren().size() > 0;
    }

    public FieldData getFieldData(FIXRepositoryHelper helper) {
        return FieldData.builder()
                .tagNum(tag)
                .messageType(messageType)
                .dictionary(dictionary)
                .helper(helper)
                .build();
    }

    @Override
    public void addToJSON(Map<String, Object> json) {
        json.put(getName(), getValue());
    }
}
