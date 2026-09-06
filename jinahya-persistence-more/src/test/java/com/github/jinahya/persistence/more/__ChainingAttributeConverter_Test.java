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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link __ChainingAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __ChainingAttributeConverter_Test {

    @DisplayName("an anonymous subclass composes two converters")
    @Test
    void __() {
        final var converter1 = __AttributeConverterUtils.<Integer, String>using(
                i -> Optional.ofNullable(i).map(Object::toString).orElse(null),
                s -> Optional.ofNullable(s).map(Integer::valueOf).orElse(null)
        );
        final var converter2 = __AttributeConverterUtils.<String, Integer>using(
                s -> Optional.ofNullable(s).map(Integer::valueOf).orElse(null),
                i -> Optional.ofNullable(i).map(Object::toString).orElse(null)
        );
        final var converter = new __ChainingAttributeConverter<Integer, String, Integer>(converter1, converter2);
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToDatabaseColumn(1)).isOne();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
        assertThat(converter.convertToEntityAttribute(1)).isOne();
    }
}
