package com.github.jinahya.persistence.mapped;

/*-
 * #%L
 * jinahya-persistence-mapped
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __DatabaseConstantsTest {

    @Test
    void constructors__() throws ReflectiveOperationException {
        {
            final var constructor = __DatabaseConstants.class.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            assertThatThrownBy(constructor::newInstance).isInstanceOf(ReflectiveOperationException.class);
        }
        for (final var declaredClass : __DatabaseConstants.class.getDeclaredClasses()) {
            final var constructor = declaredClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            assertThatThrownBy(constructor::newInstance).isInstanceOf(ReflectiveOperationException.class);
        }
    }

    @Test
    void fields__() throws IllegalAccessException {
        for (final var declaredClass : __DatabaseConstants.class.getDeclaredClasses()) {
            for (final var declaredField : declaredClass.getDeclaredFields()) {
                final var value = declaredField.get(null);
                log.debug("{}.{}: {}", declaredClass.getSimpleName(), declaredField.getName(), value);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A class for testing {@link __DatabaseConstants.__MySqlConstants}.
     */
    @Nested
    class __MySqlConstantsTest {

        @Test
        void __BIGINT() {
            log.debug("MAX_BIGINT: {}", __DatabaseConstants.__MySqlConstants.MAX_BIGINT);
        }

        @Test
        void __BIGINT_UNSIGNED() {
            log.debug("MAX_BIGINT_UNSIGNED: {}", __DatabaseConstants.__MySqlConstants.MAX_BIGINT_UNSIGNED());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A class for testing {@link __DatabaseConstants.__PostgreSqlConstants}.
     */
    @Nested
    class __PostgreSqlConstantsTest {

    }
}
