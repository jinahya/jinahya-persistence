package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JavaSqlUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __MappedEntityJdbcUtils {

    static void acceptEachColumnName(@Nonnull final Connection connection, @Nullable final String catalog,
                                     @Nullable final String schemaPattern, @Nonnull final String tableNamePattern,
                                     @Nonnull final Consumer<? super String> consumer)
            throws SQLException {
        assert connection != null;
        assert tableNamePattern != null;
        assert consumer != null;
        __JavaSqlUtils.acceptEachColumnName(
                connection,
                catalog,
                schemaPattern,
                tableNamePattern,
                consumer
        );
    }

    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    void acceptEachColumnName(@Nonnull final Connection connection, @Nonnull final Class<ENTITY> entityClass,
                              @Nonnull final Consumer<? super String> consumer)
            throws SQLException {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(connection, "connection is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("entityClass does not have @Table annotation: " + entityClass);
        }
        final var catalog = table.catalog();
        final var schemaPattern = table.schema();
        final var tableNamePattern = table.name();
        acceptEachColumnName(
                connection,
                catalog,
                schemaPattern,
                tableNamePattern,
                consumer
        );
    }

//    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
//    void acceptEachColumnName(@Nonnull final Connection connection,
//                              @Nonnull final __DatabaseMetaDataInfo databaseMetaDataInfo,
//                              @Nonnull final Class<ENTITY> entityClass,
//                              @Nonnull final Consumer<? super String> consumer)
//            throws SQLException {
//        Objects.requireNonNull(connection, "connection is null");
//        Objects.requireNonNull(entityClass, "entityClass is null");
//        Objects.requireNonNull(databaseMetaDataInfo, "databaseMetaDataInfo is null");
//        Objects.requireNonNull(consumer, "consumer is null");
//        final var table = entityClass.getAnnotation(Table.class);
//        if (table == null) {
//            throw new IllegalArgumentException("entityClass does not have @Table annotation: " + entityClass);
//        }
//        final var catalog = databaseMetaDataInfo.getCatalog();
//        final var schemaPattern = databaseMetaDataInfo.getSchema();
//        final var tableNamePattern = table.name();
//        acceptEachColumnName(
//                connection,
//                catalog,
//                schemaPattern,
//                tableNamePattern,
//                consumer
//        );
//    }
}
