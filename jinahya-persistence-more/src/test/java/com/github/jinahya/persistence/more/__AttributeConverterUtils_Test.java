package com.github.jinahya.persistence.more;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link __AttributeConverterUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class __AttributeConverterUtils_Test {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested class for testing
     * {@link __AttributeConverterUtils#using(java.util.function.Function, java.util.function.Function)}
     */
    @DisplayName("using(toDatabaseColumn, toEntityColumn)")
    @Nested
    class Using_Test {

        @Test
        void __() {
            final var converter = __AttributeConverterUtils.<Integer, String>using(
                    i -> Optional.ofNullable(i).map(Object::toString).orElse(null),
                    s -> Optional.ofNullable(s).map(Integer::valueOf).orElse(null)
            );
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(1)).isEqualTo("1");
            assertThat(converter.convertToEntityAttribute(null)).isNull();
            assertThat(converter.convertToEntityAttribute("1")).isOne();
        }
    }
}
