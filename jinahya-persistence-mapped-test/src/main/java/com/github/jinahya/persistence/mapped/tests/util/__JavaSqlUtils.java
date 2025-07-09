package com.github.jinahya.persistence.mapped.tests.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public class __JavaSqlUtils {

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
                                            @Nonnull final Consumer<? super String> consumer)
            throws SQLException {
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
                final var tableName = resultSet.getString(COLUMN_LABEL_COLUMN_NAME);
                consumer.accept(tableName);
            }
        }
    }

    public static <T extends Collection<? super String>> T addAllColumnNames(@Nonnull final Connection connection,
                                                                             @Nullable final String catalog,
                                                                             @Nullable final String schemaPattern,
                                                                             @Nonnull final String tableNamePattern,
                                                                             @Nonnull final T collection)
            throws SQLException {
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(tableNamePattern, "tableNamePattern is null");
        Objects.requireNonNull(collection, "collection is null");
        acceptEachColumnName(connection, catalog, schemaPattern, tableNamePattern, collection::add);
        return collection;
    }
}
