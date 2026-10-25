package com.github.jinahya.persistence.more.converter.util;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TimeZoneStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("TimeZoneStringAttributeConverter")
@SuppressWarnings({
        "deprecation" // the converter under test is deprecated
})
class TimeZoneStringAttributeConverter_Test {

    private final TimeZoneStringAttributeConverter converter = new TimeZoneStringAttributeConverter();

    @DisplayName("every time zone is stored as its ID, and read back")
    @ParameterizedTest
    @ValueSource(strings = {"Asia/Seoul", "America/New_York", "UTC", "GMT", "GMT+09:00", "GMT-03:30", "PST"})
    void __roundTrip(final String id) {
        final var value = TimeZone.getTimeZone(id);
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(id);
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("an ID the JDK does not know reads as GMT, as TimeZone.getTimeZone reads it")
    @ParameterizedTest
    @ValueSource(strings = {"GTM", "", "Asia/Nowhere", "+09:00", "gmt"})
    void __unknownId(final String text) {
        assertThat(converter.convertToEntityAttribute(text)).extracting(TimeZone::getID).isEqualTo("GMT");
    }
}
