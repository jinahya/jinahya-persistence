package com.github.jinahya.persistence.more.javatime;

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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the conversions of the mapped superclasses in this package, without a database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class ___MappedUtcDateTime_Test {

    @Nested
    class OfZonedDateTime {

        @DisplayName("a zoned date-time is kept as its moment in UTC and its zone, and rebuilt exactly")
        @Test
        void _roundTrip() {
            final var written = ZonedDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneId.of("Asia/Seoul"));
            final var value = new _ZonedDateTimeEmbeddable();
            value.setZonedDateTime(written);
            assertThat(value.getUtcDateTime()).isEqualTo(LocalDateTime.of(2026, 9, 20, 1, 0));
            assertThat(value.getZoneId()).isEqualTo(ZoneId.of("Asia/Seoul"));
            assertThat(value.getInstant()).isEqualTo(written.toInstant());
            assertThat(value.getZonedDateTime()).isEqualTo(written);
        }

        @DisplayName("the later of two offsets in a daylight-saving overlap survives")
        @Test
        void _roundTrip_overlap() {
            final var written = ZonedDateTime.of(2026, 11, 1, 1, 30, 0, 0, ZoneId.of("America/New_York"))
                    .withLaterOffsetAtOverlap();
            final var value = new _ZonedDateTimeEmbeddable();
            value.setZonedDateTime(written);
            assertThat(value.getZonedDateTime()).isEqualTo(written);
            assertThat(value.getZonedDateTime().getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
        }

        @DisplayName("a value incomplete in either attribute is no zoned date-time")
        @Test
        void _null() {
            final var value = new _ZonedDateTimeEmbeddable();
            assertThat(value.getZonedDateTime()).isNull();
            assertThat(value.getInstant()).isNull();
            value.setUtcDateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
            assertThat(value.getZonedDateTime()).isNull();
            value.setZonedDateTime(null);
            assertThat(value.getUtcDateTime()).isNull();
            assertThat(value.getZoneId()).isNull();
        }
    }

    @Nested
    class OfOffsetDateTime {

        @DisplayName("an offset date-time is kept as its moment in UTC and its offset, and rebuilt exactly")
        @Test
        void _roundTrip() {
            final var written = OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.ofHours(9));
            final var value = new _OffsetDateTimeEmbeddable();
            value.setOffsetDateTime(written);
            assertThat(value.getUtcDateTime()).isEqualTo(LocalDateTime.of(2026, 9, 20, 1, 0));
            assertThat(value.getZoneOffset()).isEqualTo(ZoneOffset.ofHours(9));
            assertThat(value.getInstant()).isEqualTo(written.toInstant());
            assertThat(value.getOffsetDateTime()).isEqualTo(written);
        }

        @DisplayName("an offset of seconds survives")
        @Test
        void _roundTrip_seconds() {
            final var written = OffsetDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHoursMinutesSeconds(8, 27, 52));
            final var value = new _OffsetDateTimeEmbeddable();
            value.setOffsetDateTime(written);
            assertThat(value.getOffsetDateTime()).isEqualTo(written);
        }

        @DisplayName("a value incomplete in either attribute is no offset date-time")
        @Test
        void _null() {
            final var value = new _OffsetDateTimeEmbeddable();
            assertThat(value.getOffsetDateTime()).isNull();
            value.setZoneOffset(ZoneOffset.UTC);
            assertThat(value.getOffsetDateTime()).isNull();
            value.setOffsetDateTime(null);
            assertThat(value.getUtcDateTime()).isNull();
            assertThat(value.getZoneOffset()).isNull();
        }
    }
}

