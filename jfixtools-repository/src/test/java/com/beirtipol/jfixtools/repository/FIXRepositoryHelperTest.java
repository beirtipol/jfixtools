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

import fixrepository.Field;
import fixrepository.Message;
import fixrepository.PurposeT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "./application-test.properties")
@EnableConfigurationProperties(FIXRepositoryConfig.class)
@ContextConfiguration(classes = {FIXRepositoryHelper.class})
public class FIXRepositoryHelperTest {

    @Autowired
    private FIXRepositoryHelper helper;

    @Test
    public void loadField35() {
        Optional<Field> field = helper.loadFieldInfo(35);
        assertTrue(field.isPresent());
    }

    @Test
    public void loadField35ByName() {
        Optional<Field> field = helper.loadFieldInfo("MsgType");
        assertTrue(field.isPresent());
        assertThat(helper.loadFieldInfo(35)).isEqualTo(field);
    }

    @Test
    public void loadField35Text() {
        Optional<Field> field = helper.loadFieldInfo(35);
        assertTrue(helper.getText("FIELD_35", PurposeT.SYNOPSIS).get().contains("the MsgType field"));
    }

    @Test
    public void loadMessage() {
        Optional<Message> msg = helper.loadMessageInfo("8");
        assertTrue(msg.isPresent());
        assertThat(msg.get().getName()).isEqualTo("ExecutionReport");
    }
}
