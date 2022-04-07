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

import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class allows easy traversal of an XML Document in a tree-like structure.
 */
@Slf4j
public class XMLTreeNode extends FIXTreeNode {
    private       Node                xmlNode;
    private final NamedDataDictionary dictionary;
    private       List<FIXTreeNode>   children;
    private       String              xmlString;

    public XMLTreeNode(Node xmlNode, FIXTreeNode parent, NamedDataDictionary dictionary) {
        super(parent, dictionary);
        this.xmlNode    = xmlNode;
        this.dictionary = dictionary;
    }

    @Override
    public String getTag() {
        return xmlNode.getNodeName();
    }

    @Override
    public String getName() {
        return xmlNode.getNodeName();
    }

    @Override
    public String getValue() {
        return xmlNode.getTextContent();
    }

    @Override
    public String getDescription() {
        return xmlNode.getClass().getSimpleName();
    }

    public String getXMLString() {
        if (xmlString == null) {
            StringWriter sw = new StringWriter();
            try {
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.transform(new DOMSource(xmlNode), new StreamResult(sw));
            } catch (TransformerException e) {
                log.error("Error transforming node to string", e);
            }
            xmlString = sw.toString();
            xmlString = xmlString.replaceAll("\n\\s+\n", "\n");
        }
        return xmlString;
    }

    @Override
    public List<FIXTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            NodeList childNodes = xmlNode.getChildNodes();

            if (xmlNode.hasAttributes()) {
                NamedNodeMap atts = xmlNode.getAttributes();
                for (int i = 0; i < atts.getLength(); i++) {
                    children.add(new XMLTreeNode(atts.item(i), this, dictionary));
                }
            }

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode instanceof Comment) {
                    continue;
                }
                if (!StringUtils.hasText(childNode.getTextContent())) {
                    continue;
                }
                children.add(new XMLTreeNode(childNode, this, dictionary));
            }
        }
        return children;
    }

    @Override
    public boolean hasChildren() {
        return getChildren().size() > 0;
    }

    @Override
    public void addToJSON(Map<String, Object> json) {
        json.put(getName(),getXMLString());
    }
}
