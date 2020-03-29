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

package com.beirtipol.jfixtools.repository;

import fixrepository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FIXRepositoryHelper {
    private JAXBContext   context;
    private FixRepository repository;
    private Phrases       phrases;

    @Autowired
    private FIXRepositoryConfig config;

    public FIXRepositoryHelper() {

    }

    @PostConstruct
    private void initialize() {
        try {
            log.info("Creating JAXB Context");
            context = JAXBContext.newInstance(FixRepository.class);
            log.info("Loading Repository XML");
            long start = System.nanoTime();
            repository = loadXML(config.getRepositoryFilePath());
            int nanos = 1000000;
            log.info("Loaded Repository XML in " + (System.nanoTime() - start) / nanos + "ms");
            log.info("Loading Phrases XML");
            start = System.nanoTime();
            phrases = loadXML(config.getPhrasesFilePath());
            log.info("Loaded Phrases XML in " + (System.nanoTime() - start) / nanos + "ms");
            log.info("Loaded JAXB Objects");
        } catch (JAXBException e) {
            log.error("Could not create JAXBContext", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T loadXML(String filename) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String path = FIXRepositoryHelper.class.getClassLoader().getResource(filename).getPath();
        log.info("Loading from " + path);
        return (T) unmarshaller.unmarshal(new File(path));
    }

    public FixRepository getRepository() {
        return repository;
    }

    public Optional<Field> loadFieldInfo(int tag) {
        List<Fix> fixes = repository.getFix();
        Fix fix = fixes.get(0);
        Fields fields = fix.getFields();
        Optional<Field> result = fields.getField().stream().filter(f -> f.getId().intValue() == tag).findFirst();
        return result;
    }

    public Optional<Message> loadMessageInfo(String msgType) {
        List<Fix> fixes = repository.getFix();
        Fix fix = fixes.get(0);
        Messages messages = fix.getMessages();
        Optional<Message> result = messages.getMessage().stream().filter(m -> Objects.equals(m.getMsgType(), msgType)).findFirst();
        return result;
    }

    public Optional<String> getText(String textID, PurposeT purpose) {
        return getPhrase(textID).map(phrase ->
                getText(phrase, purpose).map(text -> getText(text)
                ).orElse(Optional.empty())
        ).orElse(Optional.empty());
    }

    private Optional<String> getText(Text text) {
        return Optional.of(text.getContent().stream()//
                .filter(e -> e instanceof Element)//
                .map(e -> {
                    StringBuilder sb = new StringBuilder();
                    NodeList nl = ((Element) e).getChildNodes();
                    for (int i = 0; i < nl.getLength(); i++) {
                        sb.append(((org.w3c.dom.Text) nl.item(i)).getWholeText());
                    }
                    return sb.toString();
                }).collect(Collectors.joining("\n")));
    }

    private Optional<Text> getText(Phrase phrase, PurposeT purpose) {
        return phrase.getText()//
                .stream()//
                .filter(t -> t.getPurpose() == purpose)//
                .findFirst();
    }

    private Optional<Phrase> getPhrase(String textID) {
        return phrases.getPhrase()//
                .stream()//
                .filter(e -> e.getTextId().equals(textID))//
                .findFirst();
    }
}
