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
}
