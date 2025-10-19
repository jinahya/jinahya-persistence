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

import com.github.jinahya.database.metadata.bind.Column;
import com.github.jinahya.database.metadata.bind.Context;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilities for the {@link java.sql} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___JavaSql_TestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_LABEL_TABLE_NAME = "TABLE_NAME";

    static final String COLUMN_LABEL_COLUMN_NAME = "COLUMN_NAME";

    static final String COLUMN_LABEL_IS_NULLABLE = "IS_NULLABLE";

    static final String COLUMN_VALUE_IS_NULLABLE_YES = "YES";

    static void acceptEachTableName(@Nonnull final Connection connection, @Nullable final String catalog,
                                    @Nullable final String schema, @Nullable final String[] types,
                                    @Nonnull final Consumer<? super String> consumer)
            throws SQLException {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final String tableNamePattern = null;
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

    static <C extends Collection<? super String>> C addAllTableNames(@Nonnull final Connection connection,
                                                                     @Nullable final String catalog,
                                                                     @Nullable final String schema,
                                                                     @Nullable final String[] types,
                                                                     @Nonnull final C collection)
            throws SQLException {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachTableName(connection, catalog, schema, types, collection::add);
        return collection;
    }

    static void acceptEachColumnName(@Nonnull final Connection connection, @Nullable final String catalog,
                                     @Nullable final String schemaPattern, @Nonnull final String tableNamePattern,
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

    static void acceptEachColumnNameAndIsNullable(@Nonnull final Connection connection, @Nullable final String catalog,
                                                  @Nullable final String schemaPattern,
                                                  @Nonnull final String tableNamePattern,
                                                  @Nonnull final BiConsumer<? super String, ? super Boolean> consumer) {
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
                final var isNullable = resultSet.getString(COLUMN_LABEL_IS_NULLABLE);
                consumer.accept(
                        columnName,
                        Objects.equals(isNullable, COLUMN_VALUE_IS_NULLABLE_YES)
                );
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

    static Map<String, Boolean> getColumnNameAndIsNullable(@Nonnull final Connection connection,
                                                           @Nullable final String catalog,
                                                           @Nullable final String schemaPattern,
                                                           @Nonnull final String tableNamePattern) {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(tableNamePattern, "tableNamePattern is null");
        final var map = new HashMap<String, Boolean>();
        acceptEachColumnNameAndIsNullable(
                connection,
                catalog,
                schemaPattern,
                tableNamePattern,
                map::put
        );
        return map;
    }

    /**
     * .
     *
     * @param connection       .
     * @param catalog          .
     * @param schemaPattern    .
     * @param tableNamePattern .
     * @return .
     * @throws SQLException .
     * @see Context#getColumns(String, String, String, String)
     */
    static Map<String, Column> getBoundColumns(@Nonnull final Connection connection, @Nullable final String catalog,
                                               @Nullable final String schemaPattern,
                                               @Nonnull final String tableNamePattern)
            throws SQLException {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(tableNamePattern, "tableNamePattern is null");
        return Context.newInstance(connection)
                .getColumns(catalog, schemaPattern, tableNamePattern, "%")
                .stream()
                .collect(Collectors.toMap(Column::getColumnName, Function.identity()));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static <C extends Collection<? super String>>
    C addAllColumnNames(@Nonnull final Connection connection, @Nullable final String catalog,
                        @Nullable final String schemaPattern, @Nonnull final String tableNamePattern,
                        @Nonnull final C collection) {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(tableNamePattern, "tableNamePattern is null");
        Objects.requireNonNull(collection, "collection is null");
        acceptEachColumnName(
                connection,
                catalog,
                schemaPattern,
                tableNamePattern,
                collection::add
        );
        return collection;
    }

    static void printDatabaseInfo(final Connection connection) throws SQLException {
        final var databaseMetaData = connection.getMetaData();
//        final var databaseProductName = databaseMetaData.getDatabaseProductName();
//        final var databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
        try (var catalogs = databaseMetaData.getCatalogs()) {
            while (catalogs.next()) {
                final var tableCat = catalogs.getString("TABLE_CAT");
                logger.log(Level.INFO, "TABLE_CAT: {0}", tableCat);
                try (var schemas = databaseMetaData.getSchemas(tableCat, null)) {
                    while (schemas.next()) {
                        final var schema = schemas.getString("TABLE_SCHEM");
                        logger.log(Level.INFO, "\tTABLE_SCHEM: {0}", schema);
                    }
                } catch (final SQLException sqle) {
                    // https://bugs.mysql.com/bug.php?id=118938
                    sqle.printStackTrace(System.err);
                }
            }
        }
        try (var schemas = databaseMetaData.getSchemas()) {
            while (schemas.next()) {
                final var tableSchem = schemas.getString("TABLE_SCHEM");
                final var tableCatalog = schemas.getString("TABLE_CATALOG");
                logger.log(Level.INFO, "TABLE_SCHEM: {0}; TABLE_CATALOG: {1}", tableSchem, tableCatalog);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JavaSql_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
