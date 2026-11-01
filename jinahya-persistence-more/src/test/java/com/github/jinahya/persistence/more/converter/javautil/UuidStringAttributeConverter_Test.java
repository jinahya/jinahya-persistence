package com.github.jinahya.persistence.more.converter.javautil;

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

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link UuidStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("UuidStringAttributeConverter")
class UuidStringAttributeConverter_Test {

    private final UuidStringAttributeConverter converter = new UuidStringAttributeConverter();

    @DisplayName("a UUID is stored as its text, in lower case, and read back")
    @Test
    void __roundTrip() {
        final var value = UUID.randomUUID();
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(value.toString()).isLowerCase();
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
    }

    @DisplayName("the text of a UUID reads back in either case, as SQL Server writes it in upper case")
    @ParameterizedTest
    @ValueSource(strings = {"6F9619FF-8B86-D011-B42D-00C04FC964FF", "6f9619ff-8b86-d011-b42d-00c04fc964ff"})
    void __eitherCase(final String text) {
        final var value = converter.convertToEntityAttribute(text);
        assertThat(value).isEqualTo(UUID.fromString(text.toLowerCase(Locale.ROOT)));
        assertThat(converter.convertToDatabaseColumn(value)).isEqualToIgnoringCase(text);
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("a column which is not the text of a UUID is refused")
    @ParameterizedTest
    @ValueSource(strings = {"", "not-a-uuid", "6F9619FF8B86D011B42D00C04FC964FF"})
    void __notUuid(final String text) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
