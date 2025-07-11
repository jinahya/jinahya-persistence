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

@Slf4j
class __DatabaseConstantsTest {

    /**
     * A class for testing {@link __DatabaseConstants.__MySQLConstants}.
     */
    @Nested
    class __MySQLTest {

        @Test
        void __BIGINT() {
            log.debug("MAX_BIGINT: {}", __DatabaseConstants.__MySQLConstants.MAX_BIGINT);
        }

        @Test
        void __BIGINT_UNSIGNED() {
            log.debug("MAX_BIGINT_UNSIGNED: {}", __DatabaseConstants.__MySQLConstants.MAX_BIGINT_UNSIGNED());
        }
    }

    /**
     * A class for testing {@link __DatabaseConstants.__PostgreSQLConstants}.
     */
    @Nested
    class __PostgresTest {

    }
}
