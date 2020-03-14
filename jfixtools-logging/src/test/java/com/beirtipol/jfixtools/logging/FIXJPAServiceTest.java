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
package com.beirtipol.jfixtools.logging;

import com.beirtipol.jfixtools.logging.jpa.FIXMessageLogRepository;
import com.beirtipol.jfixtools.logging.jpa.FIXMessagesLogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@SpringBootTest
public class FIXJPAServiceTest {
    @Autowired
    private FIXMessageLogRepository repository;

    @Test
    public void givenGenericEntityRepository_whenSaveAndRetreiveEntity_thenOK() {
        FIXMessagesLogEntry message = new FIXMessagesLogEntry();
        message.setId(1l);
        message.setBeginstring("FIX4.4");
        message.setSendercompid("Sender");
        message.setTargetcompid("Target");
        message.setText("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TESTBUY1\u000152=20180920-18:14:19.508\u000156=TESTSELL1\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20180920-18:14:19.492\u000110=092\u0001");
        message.setTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        FIXMessagesLogEntry genericEntity = repository
                .save(message);
        FIXMessagesLogEntry foundEntity = repository
                .findById(genericEntity.getId()).get();

        assertNotNull(foundEntity);
        assertEquals(genericEntity, foundEntity);
    }
}
