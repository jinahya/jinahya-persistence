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

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link CurrencyStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("CurrencyStringAttributeConverter")
class CurrencyStringAttributeConverter_Test {

    private final CurrencyStringAttributeConverter converter = new CurrencyStringAttributeConverter();

    @DisplayName("every currency is stored as its ISO 4217 code, and read back as the same instance")
    @Test
    void __roundTrip() {
        assertThat(Currency.getAvailableCurrencies()).isNotEmpty().allSatisfy(c -> {
            final var column = converter.convertToDatabaseColumn(c);
            assertThat(column).isEqualTo(c.getCurrencyCode()).hasSize(3);
            assertThat(converter.convertToEntityAttribute(column)).isSameAs(c);
        });
    }

    @Test
    void __krw() {
        assertThat(converter.convertToDatabaseColumn(Currency.getInstance("KRW"))).isEqualTo("KRW");
    }

    @DisplayName("a column which is not an ISO 4217 code, in upper case, is refused")
    @ParameterizedTest
    @ValueSource(strings = {"", "krw", "KR", "KRWW", "XYZ"})
    void __notCurrency(final String text) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
