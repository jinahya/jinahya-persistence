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

import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ZoneOffsetStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("ZoneOffsetStringAttributeConverter")
class ZoneOffsetStringAttributeConverter_Test {

    private final ZoneOffsetStringAttributeConverter converter = new ZoneOffsetStringAttributeConverter();

    @DisplayName("an offset is stored as its identifier, and read back")
    @ParameterizedTest
    @ValueSource(strings = {"Z", "+09:00", "-03:30", "+05:45", "+18:00", "-18:00", "+01:02:03"})
    void __roundTrip(final String text) {
        final var value = ZoneOffset.of(text);
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(text);
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
    }

    @DisplayName("UTC is stored as Z")
    @Test
    void __utc() {
        assertThat(converter.convertToDatabaseColumn(ZoneOffset.UTC)).isEqualTo("Z");
    }

    @DisplayName("a column which is not the identifier of an offset is refused")
    @ParameterizedTest
    @ValueSource(strings = {"", "Asia/Seoul", "UTC", "+19:00", "09:00"})
    void __notOffset(final String text) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
