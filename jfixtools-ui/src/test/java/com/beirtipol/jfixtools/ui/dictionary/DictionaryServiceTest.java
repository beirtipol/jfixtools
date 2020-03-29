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

package com.beirtipol.jfixtools.ui.dictionary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {DictionaryService.class})
@TestPropertySource(locations = "./application-test.properties")
@EnableConfigurationProperties(DictionaryConfig.class)
public class DictionaryServiceTest {
    @Autowired
    private DictionaryService service;

    @Test
    public void loadDefaultDictionaries() throws Exception {
        assertThat(service.loadDefault()).hasSize(12);
    }

    @Test
    public void loadCustomDictionaries() throws Exception {
        assertThat(service.loadCustom()).hasSize(1);
    }

    @Test
    public void loadAllDictionaries() throws Exception {
        assertThat(service.loadAll()).hasSize(13);
    }
}

