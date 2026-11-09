package com.github.jinahya.persistence.more.converter.javatime;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
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

import java.time.DateTimeException;
import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link YearConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("YearConverter")
class YearConverter_Test {

    @DisplayName("OfShort: a year is stored as its value, and read back")
    @ParameterizedTest
    @ValueSource(ints = {Short.MIN_VALUE, -1, 0, 1901, 2026, 2155, Short.MAX_VALUE})
    void __ofShort_roundTrip(final int value) {
        final var converter = new YearConverter.OfShort();
        final var year = Year.of(value);
        final var column = converter.convertToDatabaseColumn(year);
        assertThat(column).isEqualTo((short) value);
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(year);
    }

    @DisplayName("OfShort: a year out of the range of a short is refused, not truncated")
    @ParameterizedTest
    @ValueSource(ints = {Short.MIN_VALUE - 1, Short.MAX_VALUE + 1, Year.MIN_VALUE, Year.MAX_VALUE})
    void __ofShort_outOfRange(final int value) {
        final var converter = new YearConverter.OfShort();
        assertThatThrownBy(() -> converter.convertToDatabaseColumn(Year.of(value)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("OfInteger: every year is stored as its value, and read back")
    @ParameterizedTest
    @ValueSource(ints = {Year.MIN_VALUE, 0, 2026, Year.MAX_VALUE})
    void __ofInteger_roundTrip(final int value) {
        final var converter = new YearConverter.OfInteger();
        final var year = Year.of(value);
        final var column = converter.convertToDatabaseColumn(year);
        assertThat(column).isEqualTo(value);
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(year);
    }

    @DisplayName("OfInteger: a column outside the range of Year is refused")
    @Test
    void __ofInteger_columnOutOfRange() {
        final var converter = new YearConverter.OfInteger();
        assertThatThrownBy(() -> converter.convertToEntityAttribute(Integer.MAX_VALUE))
                .isInstanceOf(DateTimeException.class);
    }

    @DisplayName("null converts to null, both ways")
    @Test
    void __null() {
        assertThat(new YearConverter.OfShort().convertToDatabaseColumn(null)).isNull();
        assertThat(new YearConverter.OfShort().convertToEntityAttribute(null)).isNull();
        assertThat(new YearConverter.OfInteger().convertToDatabaseColumn(null)).isNull();
        assertThat(new YearConverter.OfInteger().convertToEntityAttribute(null)).isNull();
    }
}
