package com.github.jinahya.persistence.mapped;

/*-
 * #%L
 * jinahya-persistence-mapped
 * %%
 * Copyright (C) 2025 Jinahya, Inc.
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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class __ConcreteMappedEntityWithGeneratedIdentityTest {

    __ConcreteMappedEntityWithGeneratedIdentityTest() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("toString()!blank")
    @Test
    void toString_NotBlank_() {
        final var instance = new __ConcreteMappedEntityWithGeneratedIdentity();
        final var string = instance.toString();
        assertThat(string).isNotBlank();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("equals/hashCode")
    @Test
    void equals_verify_() {
        EqualsVerifier
                .forClass(__ConcreteMappedEntityWithGeneratedIdentity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .suppress(Warning.SURROGATE_KEY)
//                .suppress(Warning.JPA_GETTER) // https://github.com/jqno/equalsverifier/issues/1102
                .verify();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void getId____() {
        final var instance = new __ConcreteMappedEntityWithGeneratedIdentity();
        final var id__ = instance.getId__();
    }
}
