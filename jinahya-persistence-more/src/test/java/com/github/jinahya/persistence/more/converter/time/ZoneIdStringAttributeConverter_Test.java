package com.github.jinahya.persistence.more.converter.time;

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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ZoneIdStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("ZoneIdStringAttributeConverter")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class ZoneIdStringAttributeConverter_Test {

    private final ZoneIdStringAttributeConverter converter =
            new ZoneIdStringAttributeConverter();

    @DisplayName("every zone is stored as its identifier, and read back")
    @ParameterizedTest
    @ValueSource(strings = {"Asia/Seoul", "America/New_York", "UTC", "Z", "+09:00", "-03:30", "UTC+09:00", "GMT"})
    void __roundTrip(final String id) {
        final var value = ZoneId.of(id);
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(value.getId());
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
    }

    @DisplayName("an offset reads back as a ZoneOffset")
    @Test
    void __offset() {
        assertThat(converter.convertToEntityAttribute("+09:00")).isEqualTo(ZoneOffset.ofHours(9));
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("text which identifies no zone is refused")
    @ParameterizedTest
    @ValueSource(strings = {"", "Asia/Nowhere", "+25:00", "not a zone"})
    void _IllegalArgumentException_NotAZone(final String text) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("TimeZoneStringAttributeConverter")
    @Nested
    class TimeZoneStringAttributeConverter_Test {

        private final ZoneIdStringAttributeConverter.TimeZoneStringAttributeConverter converter =
                new ZoneIdStringAttributeConverter.TimeZoneStringAttributeConverter();

        @DisplayName("a time zone is stored as the identifier of its zone, and read back")
        @ParameterizedTest
        @ValueSource(strings = {"Asia/Seoul", "America/New_York", "UTC", "GMT", "GMT+09:00", "GMT-03:30"})
        void __roundTrip(final String id) {
            final var value = TimeZone.getTimeZone(id);
            final var column = converter.convertToDatabaseColumn(value);
            assertThat(column).isEqualTo(id);
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
        }

        @DisplayName("a three-letter ID is stored as the region it stands for")
        @Test
        void __shortId() {
            final var value = TimeZone.getTimeZone("PST");
            final var column = converter.convertToDatabaseColumn(value);
            assertThat(column).isEqualTo("America/Los_Angeles");
            assertThat(converter.convertToEntityAttribute(column).hasSameRules(value)).isTrue();
        }

        @DisplayName("any zone identifier reads, an offset as a GMT custom ID")
        @Test
        void __zoneIds() {
            assertThat(converter.convertToEntityAttribute("Z")).extracting(TimeZone::getID).isEqualTo("UTC");
            assertThat(converter.convertToEntityAttribute("+09:00")).extracting(TimeZone::getID)
                    .isEqualTo("GMT+09:00");
        }

        @Test
        void __null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("an ID no zone has is refused, as the core converter refuses it")
        @ParameterizedTest
        @ValueSource(strings = {"GTM", "", "Asia/Nowhere"})
        void _IllegalArgumentException_UnknownId(final String text) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
