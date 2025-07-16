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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class __ConcreteMappedEntityTest {

    @Test
    void toString__() {
        final var instance = new __ConcreteMappedEntity();
        final var string = instance.toString();
        assertThat(string).isNotBlank();
    }

    @Test
    void equals__() {
        final var instance1 = new __ConcreteMappedEntity();
        {
            assertThat(instance1).isEqualTo(instance1);
            assertThat(instance1).isNotEqualTo(new Object());
        }
        final var instance2 = new __ConcreteMappedEntity();
        assertThat(instance1).isNotEqualTo(instance2);
        assertThat(instance2).isNotEqualTo(instance1);
        instance1.setId__(0L);
        instance2.setId__(0L);
        assertThat(instance1).isEqualTo(instance2);
        assertThat(instance2).isEqualTo(instance1);
    }

    @Test
    void getId____() {
        final var instance = new __ConcreteMappedEntity();
        final var id__ = instance.getId__();
    }

    @Test
    void id____() {
        final var instance = new __ConcreteMappedEntity();
        final var result = instance.id__(0L);
        assertThat(result).isSameAs(instance);
    }
}
