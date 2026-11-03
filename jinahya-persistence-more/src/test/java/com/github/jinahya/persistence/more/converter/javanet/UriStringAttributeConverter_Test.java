package com.github.jinahya.persistence.more.converter.javanet;

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

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link UriStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("UriStringAttributeConverter")
class UriStringAttributeConverter_Test {

    private final UriStringAttributeConverter converter = new UriStringAttributeConverter();

    @DisplayName("a URI is stored as its text, and read back as an equal one")
    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com/a%20b?q=1#f",
            "mailto:someone@example.com",
            "urn:isbn:0451450523",
            "../relative/path",
            "",
            "https://[::1]:8080/"
    })
    void __roundTrip(final String text) {
        final var value = URI.create(text);
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(text);
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
    }

    @DisplayName("a column which is not a URI is refused")
    @ParameterizedTest
    @ValueSource(strings = {"https://example.com/a b", "https://example.com/%zz", ":no-scheme"})
    void __notUri(final String text) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
