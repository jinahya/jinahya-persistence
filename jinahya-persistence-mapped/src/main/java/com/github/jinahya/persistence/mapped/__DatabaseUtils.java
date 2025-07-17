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

/**
 * Utilities for databases.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __DatabaseUtils {

    // ----------------------------------------------------------------------------------------------------------- MySQL

    /**
     * Utilities for the MySQL.
     */
    public static final class __MySqlUtils {

        private __MySqlUtils() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // ---------------------------------------------------------------------------------------------------------- Oracle

    /**
     * Utilities for the Oracle.
     */
    public static final class __OracleUtils {

        private __OracleUtils() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // ------------------------------------------------------------------------------------------------------ PostgreSQL

    /**
     * Utilities for the PostgreSQL.
     */
    public static final class __PostgreSqlUtils {

        private __PostgreSqlUtils() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __DatabaseUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
