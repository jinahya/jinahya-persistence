package com.github.jinahya.persistence.mapped.tests.util;

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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class __JavaSqlTestUtils {

    @Nested
    class AcceptEachTableNameTest {

        @Test
        void __() throws SQLException {
            final var schema = "schema";
            final var catalog = "catalog";
            final var tableNames = List.of("a", "b", "c");
            final var connection = mock(Connection.class);
            {
                final var databaseMetaData = mock(DatabaseMetaData.class);
                when(connection.getMetaData()).thenReturn(databaseMetaData);
                final var resultSet = mock(ResultSet.class);
                final var removableTableNames = new ArrayList<>(tableNames);
                when(resultSet.next()).thenAnswer((Answer<Boolean>) i -> !removableTableNames.isEmpty());
                when(resultSet.getString(__JavaSqlUtils.COLUMN_LABEL_TABLE_NAME))
                        .thenAnswer((Answer<String>) i -> removableTableNames.removeFirst());
                when(databaseMetaData.getTables(catalog, schema, null, null)).thenReturn(resultSet);
            }
            final var consumer = (Consumer<? super String>) mock(Consumer.class);
            // ---------------------------------------------------------------------------------------------------- when
            __JavaSqlUtils.acceptEachTableName(
                    connection,
                    catalog,
                    schema,
                    consumer
            );
            // ---------------------------------------------------------------------------------------------------- then
            final var tableNameCaptor = ArgumentCaptor.forClass(String.class);
            verify(consumer, times(tableNames.size())).accept(tableNameCaptor.capture());
            final var acceptedTableNames = tableNameCaptor.getAllValues();
            assertThat(acceptedTableNames).isEqualTo(tableNames);
        }
    }

    @Nested
    class AddEachTableNameTest {

        @Test
        void __() throws SQLException {
            final var schema = "schema";
            final var catalog = "catalog";
            final var tableNames = List.of("a", "b", "c");
            final var connection = mock(Connection.class);
            {
                final var databaseMetaData = mock(DatabaseMetaData.class);
                when(connection.getMetaData()).thenReturn(databaseMetaData);
                final var resultSet = mock(ResultSet.class);
                final var removableTableNames = new ArrayList<>(tableNames);
                when(resultSet.next()).thenAnswer((Answer<Boolean>) i -> !removableTableNames.isEmpty());
                when(resultSet.getString(__JavaSqlUtils.COLUMN_LABEL_TABLE_NAME))
                        .thenAnswer((Answer<String>) i -> removableTableNames.removeFirst());
                when(databaseMetaData.getTables(catalog, schema, null, null)).thenReturn(resultSet);
            }
            // ---------------------------------------------------------------------------------------------------- when
            final var collection = __JavaSqlUtils.addAllTableNames(
                    connection,
                    catalog,
                    schema,
                    new ArrayList<>()
            );
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(collection).isEqualTo(tableNames);
        }
    }

    @Nested
    class AcceptEachColumnNameTest {

        @Test
        void __() throws SQLException {
            // --------------------------------------------------------------------------------------------------- given
            final var catalog = "catalog";
            final var schemaPattern = "schemaPattern";
            final var tableNamePattern = "tableNamePattern";
            final var columnNames = List.of("a", "b", "c");
            final var connection = mock(Connection.class);
            {
                final var databaseMetaData = mock(DatabaseMetaData.class);
                when(connection.getMetaData()).thenReturn(databaseMetaData);
                final var resultSet = mock(ResultSet.class);
                final var removableColumnNames = new ArrayList<>(columnNames);
                when(resultSet.next()).thenAnswer((Answer<Boolean>) i -> !removableColumnNames.isEmpty());
                when(resultSet.getString(__JavaSqlUtils.COLUMN_LABEL_COLUMN_NAME))
                        .thenAnswer((Answer<String>) i -> removableColumnNames.removeFirst());
                when(databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, "%")).thenReturn(resultSet);
            }
            final var consumer = (Consumer<? super String>) mock(Consumer.class);
            // ---------------------------------------------------------------------------------------------------- when
            __JavaSqlUtils.acceptEachColumnName(
                    connection,
                    catalog,
                    schemaPattern,
                    tableNamePattern,
                    consumer
            );
            // ---------------------------------------------------------------------------------------------------- then
            final var tableNameCaptor = ArgumentCaptor.forClass(String.class);
            verify(consumer, times(columnNames.size())).accept(tableNameCaptor.capture());
            final var acceptedTableNames = tableNameCaptor.getAllValues();
            assertThat(acceptedTableNames).isEqualTo(columnNames);
        }
    }

    @Nested
    class AddAllColumnNameTest {

        @Test
        void __() throws SQLException {
            // --------------------------------------------------------------------------------------------------- given
            final var catalog = "catalog";
            final var schemaPattern = "schemaPattern";
            final var tableNamePattern = "tableNamePattern";
            final var columnNames = List.of("a", "b", "c");
            final var connection = mock(Connection.class);
            {
                final var databaseMetaData = mock(DatabaseMetaData.class);
                when(connection.getMetaData()).thenReturn(databaseMetaData);
                final var resultSet = mock(ResultSet.class);
                final var removableColumnNames = new ArrayList<>(columnNames);
                when(resultSet.next()).thenAnswer((Answer<Boolean>) i -> !removableColumnNames.isEmpty());
                when(resultSet.getString(__JavaSqlUtils.COLUMN_LABEL_COLUMN_NAME))
                        .thenAnswer((Answer<String>) i -> removableColumnNames.removeFirst());
                when(databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, "%")).thenReturn(resultSet);
            }
            // ---------------------------------------------------------------------------------------------------- when
            final var collection = __JavaSqlUtils.addAllColumnNames(
                    connection,
                    catalog,
                    schemaPattern,
                    tableNamePattern,
                    new ArrayList<>()
            );
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(collection).isEqualTo(columnNames);
        }
    }
}
