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
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntityPersistenceIntegrationIT<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___MappedEntityPersistenceTest<ENTITY, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityPersistenceIntegrationIT(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("all table columns are mapped")
    @Test
    protected void _Mapped_AllTableColumnNames() {
        final var table = __MappedEntityTestUtils.getTableAnnotation(entityClass);
        final var catalog = applyEntityManagerFactory(___MappedEntityPersistenceTestUtils::getDefaultCatalog)
                .orElseGet(table::catalog);
        final var schema = applyEntityManagerFactory(___MappedEntityPersistenceTestUtils::getDefaultSchema)
                .orElseGet(table::schema);
        final var tableColumnNames = applyConnectionInTransactionAndRollback(
                c -> __JavaSqlUtils.addAllColumnNames(
                        c,
                        catalog,
                        schema,
                        table.name(),
                        new ArrayList<>()
                )
        );
        assertThat(tableColumnNames)
                .as("table column names for " + entityClass)
                .isNotEmpty();
        final var attributeColumnNames = applyEntityManagerFactory(
                emf -> __MappedEntityPersistenceTestUtils.addAllAttributeColumNames(
                        emf,
                        entityClass,
                        new ArrayList<>()
                )
        );
        assertThat(attributeColumnNames)
                .as("attribute column names for " + entityClass)
                .isNotEmpty();
        // -------------------------------------------------------------------------------------------------------------
        tableColumnNames.removeAll(attributeColumnNames);
        // -------------------------------------------------------------------------------------------------------------
        assertThat(tableColumnNames)
                .as("remaining table column names for " + entityClass)
                .isEmpty();
    }

    @DisplayName("all entity columns are mapped")
    @Test
    protected void _Mapped_AllEntityColumnColumnNames() {
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    final EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    final EntityManager getEntityManager() {
        return entityManager;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.Integration__
    @Inject
    private EntityManagerFactory entityManagerFactory;

    @__PersistenceProducer.Integration__
    @Inject
    private EntityManager entityManager;
}
