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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utilities for the {@link __MappedEntity_PersistenceTest}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
        "java:S6905" // SQL queries should retrieve only necessary fields
})
public class __DatabaseTestUtils {

    public static class Oracle {

        /**
         * .
         *
         * @param connection the connection.
         * @param consumer   the consumer.
         * @see <a
         *         href="https://docs.oracle.com/en/database/oracle/oracle-database/19/refrn/USER_TAB_PRIVS.html">USER_SYS_PRIVS</a>
         */
        public void USER_SYS_PRIVS(@Nonnull final Connection connection,
                                   @Nonnull final Consumer<? super ResultSet> consumer)
                throws SQLException {
            Objects.requireNonNull(connection, "connection is null");
            Objects.requireNonNull(consumer, "consumer is null");
            try (var statement = connection.createStatement()) {
                try (var resultSet = statement.executeQuery("SELECT * FROM USER_SYS_PRIVS")) {
                    consumer.accept(resultSet);
                }
            }
        }

        public List<String> USER_SYS_PRIVS__PRIVILEGES(@Nonnull final Connection connection) throws SQLException {
            final var privileges = new ArrayList<String>();
            USER_SYS_PRIVS(
                    connection,
                    r -> {
                        try {
                            while (r.next()) {
                                privileges.add(r.getString("PRIVILEGE"));
                            }
                        } catch (final SQLException sqle) {
                            throw new RuntimeException(sqle);
                        }
                    }
            );
            return privileges;
        }

        /**
         * .
         *
         * @param connection the connection.
         * @param consumer   the consumer.
         * @see <a
         *         href="https://docs.oracle.com/en/database/oracle/oracle-database/19/refrn/USER_TAB_PRIVS.html">USER_TAB_PRIVS</a>
         */
        public void USER_TAB_PRIVS(@Nonnull final Connection connection,
                                   @Nonnull final Consumer<? super ResultSet> consumer)
                throws SQLException {
            Objects.requireNonNull(connection, "connection is null");
            Objects.requireNonNull(consumer, "consumer is null");
            try (var statement = connection.createStatement()) {
                try (var resultSet = statement.executeQuery("SELECT * FROM USER_TAB_PRIVS")) {
                    consumer.accept(resultSet);
                }
            }
        }

        public List<String> USER_TAB_PRIVS__PRIVILEGES(@Nonnull final Connection connection) throws SQLException {
            final var privileges = new ArrayList<String>();
            USER_TAB_PRIVS(
                    connection,
                    r -> {
                        try {
                            while (r.next()) {
                                privileges.add(r.getString("PRIVILEGE"));
                            }
                        } catch (final SQLException sqle) {
                            throw new RuntimeException(sqle);
                        }
                    }
            );
            return privileges;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __DatabaseTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
