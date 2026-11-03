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

import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link BitSetBytesAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("BitSetBytesAttributeConverter")
class BitSetBytesAttributeConverter_Test {

    private final BitSetBytesAttributeConverter converter = new BitSetBytesAttributeConverter();

    @DisplayName("bit n is bit n % 8 of byte n / 8, and the bytes end at the last set bit")
    @Test
    void __layout() {
        final var value = new BitSet();
        value.set(0);
        value.set(9);
        assertThat(converter.convertToDatabaseColumn(value)).containsExactly(0x01, 0x02);
    }

    @DisplayName("an empty set is stored as no bytes, and read back as an empty set")
    @Test
    void __empty() {
        assertThat(converter.convertToDatabaseColumn(new BitSet())).isEmpty();
        assertThat(converter.convertToEntityAttribute(new byte[0])).isEqualTo(new BitSet());
    }

    @DisplayName("a set is read back as an equal one")
    @Test
    void __roundTrip() {
        for (int i = 0; i < 128; i++) {
            final var value = new BitSet();
            ThreadLocalRandom.current().ints(ThreadLocalRandom.current().nextInt(32), 0, 1024).forEach(value::set);
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(value)))
                    .isEqualTo(value);
        }
    }

    @DisplayName("trailing zero bytes of a column read back as no bits")
    @Test
    void __trailingZeros() {
        final var value = converter.convertToEntityAttribute(new byte[] {0x01, 0x00, 0x00});
        assertThat(value.cardinality()).isOne();
        assertThat(converter.convertToDatabaseColumn(value)).containsExactly(0x01);
    }

    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
