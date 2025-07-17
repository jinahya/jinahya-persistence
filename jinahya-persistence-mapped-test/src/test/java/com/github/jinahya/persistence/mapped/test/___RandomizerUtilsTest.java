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
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class ___RandomizerUtilsTest {

    private static class Pojo {

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @NotBlank
        private String name;
    }

    private static class PojoRandomizer extends ___Randomizer<Pojo> {

        PojoRandomizer() {
            super(Pojo.class);
        }

        @Nonnull
        @Override
        protected DataProviderStrategy getDataProviderStrategy() {
            return super.getDataProviderStrategy();
        }

        @Nonnull
        @Override
        protected PodamFactory getPodamFactory() {
            return super.getPodamFactory();
        }

        @Nonnull
        @Override
        public Pojo get() {
            return super.get();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void __() {
        final var instance = ___RandomizerUtils.newRandomizedInstanceOf(Pojo.class);
        assertThat(instance).hasValueSatisfying(v -> {
            assertThat(v.name).isNotBlank();
        });
    }
}
