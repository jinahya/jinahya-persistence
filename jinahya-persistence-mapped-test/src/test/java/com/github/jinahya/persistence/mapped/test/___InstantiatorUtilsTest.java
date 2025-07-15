package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ___InstantiatorUtilsTest {

    private static class Pojo {

        private Pojo(final String name) {
            super();
            this.name = name;
        }

        private Pojo() {
            this(null);
        }

        public String getName() {
            return name;
        }

        private final String name;
    }

    private static class PojoInstantiator extends ___Instantiator<Pojo> {

        PojoInstantiator() {
            super(Pojo.class);
        }

        @Nonnull
        @Override
        public Pojo get() {
            return new Pojo("name");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void __() {
        final var instance = ___InstantiatorUtils.newInstantiatedInstanceOf(Pojo.class);
        assertThat(instance).isPresent();
    }
}
