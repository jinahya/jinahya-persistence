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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __DatabaseUtilsTest {

    @Test
    void constructors__() throws ReflectiveOperationException {
        {
            final var constructor = __DatabaseUtils.class.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            assertThatThrownBy(constructor::newInstance).isInstanceOf(ReflectiveOperationException.class);
        }
        for (final var declaredClass : __DatabaseUtils.class.getDeclaredClasses()) {
            final var constructor = declaredClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            assertThatThrownBy(constructor::newInstance).isInstanceOf(ReflectiveOperationException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A class for testing {@link __DatabaseUtils.__MySqlUtils}.
     */
    @Nested
    class __MySqlUtilsTest {

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A class for testing {@link __DatabaseUtils.__PostgreSqlUtils}.
     */
    @Nested
    class __PostgresSqlUtilsTest {

    }
}
