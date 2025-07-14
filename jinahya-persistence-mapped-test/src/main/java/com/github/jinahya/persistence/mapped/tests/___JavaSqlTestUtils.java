package com.github.jinahya.persistence.mapped.tests;

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
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public final class ___JavaSqlTestUtils {

    static final String COLUMN_LABEL_TABLE_NAME = "TABLE_NAME";

    static final String COLUMN_LABEL_COLUMN_NAME = "COLUMN_NAME";

    public static void acceptEachTableName(@Nonnull final Connection connection,
                                           @Nonnull final String catalog, @Nonnull final String schema,
                                           @Nonnull final Consumer<? super String> consumer)
            throws SQLException {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(catalog, "catalog is null");
        Objects.requireNonNull(schema, "schema is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final String tableNamePattern = null;
        final String[] types = null;
        try (var resultSet = connection.getMetaData().getTables(
                catalog,
                schema,
                tableNamePattern,
                types
        )) {
            while (resultSet.next()) {
                final var tableName = resultSet.getString(COLUMN_LABEL_TABLE_NAME);
                consumer.accept(tableName);
            }
        }
    }

    public static <T extends Collection<? super String>> T addAllTableNames(@Nonnull final Connection connection,
                                                                            @Nonnull final String catalog,
                                                                            @Nonnull final String schema,
                                                                            @Nonnull final T collection)
            throws SQLException {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(catalog, "catalog is null");
        Objects.requireNonNull(schema, "schema is null");
        Objects.requireNonNull(collection, "collection is null");
        acceptEachTableName(connection, catalog, schema, collection::add);
        return collection;
    }

    public static void acceptEachColumnName(@Nonnull final Connection connection,
                                            @Nullable final String catalog, @Nullable final String schemaPattern,
                                            @Nonnull final String tableNamePattern,
                                            @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(tableNamePattern, "tableNamePattern is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var columnNamePattern = "%";
        try (var resultSet = connection.getMetaData().getColumns(
                catalog,
                schemaPattern,
                tableNamePattern,
                columnNamePattern
        )) {
            while (resultSet.next()) {
                final var columnName = resultSet.getString(COLUMN_LABEL_COLUMN_NAME);
                consumer.accept(columnName);
            }
        } catch (final SQLException sqle) {
            throw new RuntimeException(
                    "failed to get columns" +
                    "; catalog: " + catalog +
                    "; schemaPattern: " + schemaPattern +
                    "; tableNamePattern: " + tableNamePattern,
                    sqle
            );
        }
    }

    public static <C extends Collection<? super String>>
    C addAllColumnNames(@Nonnull final Connection connection, @Nullable final String catalog,
                        @Nullable final String schemaPattern, @Nonnull final String tableNamePattern,
                        @Nonnull final C collection) {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(tableNamePattern, "tableNamePattern is null");
        Objects.requireNonNull(collection, "collection is null");
        acceptEachColumnName(connection, catalog, schemaPattern, tableNamePattern, collection::add);
        return collection;
    }

    public static void printDatabaseInfo(final Connection connection) throws SQLException {
        final var databaseMetaData = connection.getMetaData();
        final var databaseProductName = databaseMetaData.getDatabaseProductName();
        final var databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
        System.out.println("databaseProductName = " + databaseProductName);
        System.out.println("databaseProductVersion = " + databaseProductVersion);
        try (var resultSet = databaseMetaData.getCatalogs()) {
            while (resultSet.next()) {
                final var tableCat = resultSet.getString("TABLE_CAT");
                System.out.println("TABLE_CAT: " + tableCat);
                try (var resultSet2 = databaseMetaData.getSchemas(tableCat, null)) {
                    while (resultSet2.next()) {
                        final var schema = resultSet2.getString("TABLE_SCHEM");
                        System.out.println("\tTABLE_SCHEM: " + schema);
                    }
                }
            }
        }
        try (var resultSet = databaseMetaData.getSchemas()) {
            while (resultSet.next()) {
                final var tbleSchem = resultSet.getString("TABLE_SCHEM");
                System.out.println("TABLE_SCHEM: " + tbleSchem);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JavaSqlTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
