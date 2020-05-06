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

import com.beirtipol.jfixtools.repository.FIXRepositoryHelper;
import com.beirtipol.jfixtools.ui.dictionary.DictionaryService;
import com.beirtipol.jfixtools.ui.dictionary.NamedDataDictionary;
import com.beirtipol.jfixtools.ui.util.MessageUtils;
import com.beirtipol.jfixtools.ui.vaadin.components.FIXDictionaryCombo;
import com.beirtipol.jfixtools.ui.vaadin.components.FIXMessageViewer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UIScope
@Component
@Route(value = "parser")
public class FIXParserView extends VerticalLayout {

    private final Pattern            dictionaryPattern = Pattern.compile("8=(.*?)\u0001(.*|\n)+", Pattern.MULTILINE);
    private       FIXMessageViewer   messageViewer;
    private       FIXDictionaryCombo dictionaryCombo;
    private final HorizontalLayout   viewPane;
    private final TextArea           textArea;
    private final Button             toggleButton;

    boolean fullView = true;
    private static final String FULL_VIEW = "<<";
    private static final String MINI      = ">>";

    @Autowired
    public FIXParserView(FIXRepositoryHelper repositoryHelper,
                         DictionaryService dictionaryService,
                         MessageUtils messageUtils) {
        setSizeFull();

        textArea = new TextArea();

        HorizontalLayout toolbar = new HorizontalLayout();
        add(toolbar);
        toggleButton = new Button(FULL_VIEW, e -> toggleView());
        toolbar.add(toggleButton);

        dictionaryCombo = new FIXDictionaryCombo(dictionaryService);
        toolbar.add(dictionaryCombo);
        toolbar.add(new Button("Parse", e -> {
            try {
                DataDictionary dict = getSelectedDictionary();
                Message message = messageUtils.parse(dict, textArea.getValue());
                setMessage(message, repositoryHelper);
            } catch (ConfigError | InvalidMessage e1) {
                Notification.show(e1.getMessage());
            }
        }));
        toolbar.add(new Button("Replace Pipe with SOH", e -> textArea.setValue(messageUtils.replacePipeWithSOH(textArea.getValue()))));

        toolbar.add(new Button("Correct Checksum", e -> textArea.setValue(messageUtils.correctChecksum(textArea.getValue()))));
        viewPane = new HorizontalLayout();
        viewPane.setSizeFull();
        add(viewPane);


        textArea.setSizeFull();
        textArea.addValueChangeListener(event -> {
            String value = event.getValue();
            if (dictionaryCombo.getValue() == null) {
                Matcher matcher = dictionaryPattern.matcher(value);
                if (matcher.matches()) {
                    String dictionaryName = matcher.group(1);
                    dictionaryName = dictionaryName.replaceAll("\\.", "") + ".xml";
                    dictionaryCombo.choose(dictionaryName);
                }

            }

        });
        viewPane.add(textArea);


        messageViewer = new FIXMessageViewer();
        messageViewer.setSizeFull();
        viewPane.add(messageViewer);

        textArea.setWidth("100%");
        messageViewer.setWidth("100%");
    }

    private void toggleView() {
        fullView = !fullView;
        textArea.setVisible(fullView);
        toggleButton.setText(fullView ? FULL_VIEW : MINI);
    }

    private NamedDataDictionary getSelectedDictionary() throws ConfigError {
        return dictionaryCombo.getValue();
    }

    private void setMessage(Message message, FIXRepositoryHelper repositoryHelper) {
        try {
            NamedDataDictionary dictionary = getSelectedDictionary();
            messageViewer.setContent(dictionary, message, repositoryHelper);
        } catch (ConfigError e) {
            Notification.show(e.getMessage());
        }
    }
}
