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

import fixrepository.FixRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

@Slf4j
@Service
public class FixRepositoryJAXB {

    private JAXBContext context;

    @PostConstruct
    private void initialize() {
        try {
            log.info("Creating JAXB Context");
            context = JAXBContext.newInstance(FixRepository.class);
        } catch (JAXBException e) {
            log.error("Could not create JAXBContext", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T loadXML(String filename) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        log.info("Loading from " + filename);
        InputStream resource = FixRepositoryJAXB.class.getClassLoader().getResourceAsStream(filename);
        return (T) unmarshaller.unmarshal(resource);
    }
}
