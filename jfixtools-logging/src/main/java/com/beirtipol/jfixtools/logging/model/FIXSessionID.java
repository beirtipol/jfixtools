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

package com.beirtipol.jfixtools.logging.model;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.trim;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Embeddable
@ConfigurationProperties
public class FIXSessionID implements Serializable {
    @EqualsAndHashCode.Include
    @Column(name = "beginstring")
    private String beginString;
    @EqualsAndHashCode.Include
    @Column(name = "sendercompid")
    private String senderCompID;
    @EqualsAndHashCode.Include
    @Column(name = "sendersubid")
    private String senderSubID;
    @EqualsAndHashCode.Include
    @Column(name = "senderlocid")
    private String senderLocID;
    @EqualsAndHashCode.Include
    @Column(name = "targetcompid")
    private String targetCompID;
    @EqualsAndHashCode.Include
    @Column(name = "targetsubid")
    private String targetSubID;
    @EqualsAndHashCode.Include
    @Column(name = "targetlocid")
    private String targetLocID;
    @EqualsAndHashCode.Include
    @Column(name = "session_qualifier")
    private String sessionQualifier;

    @Column(name = "creation_time")
    private LocalDateTime creationTime;
    @Column(name = "incoming_seqnum")
    private int           incomingSequenceNumber;
    @Column(name = "outgoing_seqnum")
    private int           outgoingSequenceNumber;

    public boolean matches(FIXSessionID other) {
        return matches(FIXSessionID::getBeginString, other) &&
                matches(FIXSessionID::getSenderCompID, other) &&
                matches(FIXSessionID::getSenderSubID, other) &&
                matches(FIXSessionID::getSenderLocID, other) &&
                matches(FIXSessionID::getTargetCompID, other) &&
                matches(FIXSessionID::getTargetSubID, other) &&
                matches(FIXSessionID::getTargetLocID, other) &&
                matches(FIXSessionID::getSessionQualifier, other);
    }

    /**
     * Initializing from a Database vs loading from Config files means you can have blanks, nulls or padded strings. We need to default to empty and truncate
     *
     * @param func  The method to invoke on ourselves and the other
     * @param other The object we are comparing to
     * @return Whether the fields match on each object
     */
    private boolean matches(Function<FIXSessionID, String> func, FIXSessionID other) {
        return Objects.equals(
                trim(defaultString(func.apply(this))),
                trim(defaultString(func.apply(other))));
    }


}
