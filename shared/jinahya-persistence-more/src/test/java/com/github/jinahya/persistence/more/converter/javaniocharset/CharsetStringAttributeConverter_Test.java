package com.github.jinahya.persistence.more.converter.javaniocharset;

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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link CharsetStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("CharsetStringAttributeConverter")
class CharsetStringAttributeConverter_Test {

    private final CharsetStringAttributeConverter converter = new CharsetStringAttributeConverter();

    @DisplayName("every charset is stored as its canonical name, and read back")
    @Test
    void __roundTrip() {
        assertThat(Charset.availableCharsets().values()).isNotEmpty().allSatisfy(c -> {
            final var column = converter.convertToDatabaseColumn(c);
            assertThat(column).isEqualTo(c.name());
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(c);
        });
    }

    @DisplayName("an alias, in either case, reads back as the charset, and is stored as its canonical name")
    @ParameterizedTest
    @ValueSource(strings = {"UTF-8", "utf-8", "utf8", "UTF8"})
    void __alias(final String text) {
        final var value = converter.convertToEntityAttribute(text);
        assertThat(value).isEqualTo(StandardCharsets.UTF_8);
        assertThat(converter.convertToDatabaseColumn(value)).isEqualTo("UTF-8");
    }

    @DisplayName("a column which names no charset is refused")
    @ParameterizedTest
    @ValueSource(strings = {"", "no-such-charset", "UTF 8"})
    void __notCharset(final String text) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
