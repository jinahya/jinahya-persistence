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

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntityPersistenceIT<ENTITY extends __MappedEntity<ID>, ID>
        extends ___MappedEntityPersistenceTest<ENTITY, ID> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityPersistenceIT(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("assert that all table columns are mapped")
    @Test
    protected void _Mapped_AllTableColumnNames() {
        final var table = __MappedEntityTestUtils.getTableAnnotation(entityClass);
        final var catalog = applyEntityManagerFactory(__PersistenceProducerUtils::getDefaultCatalog)
                .orElseGet(table::catalog);
        final var schema = applyEntityManagerFactory(__PersistenceProducerUtils::getDefaultSchema)
                .orElseGet(table::schema);
        final var tableColumnNames = applyConnectionInTransactionAndRollback(
                c -> ___JavaSqlTestUtils.addAllColumnNames(
                        c,
                        catalog,
                        schema,
                        table.name(),
                        new ArrayList<>()
                )
        );
        logger.log(Level.DEBUG, "tableColumnNames: {0}", tableColumnNames);
        assertThat(tableColumnNames)
                .as("table column names for " + entityClass +
                    "; catalog: " + catalog +
                    "; schema: " + schema +
                    "; table: " + table.name()
                )
                .isNotEmpty();
        final var attributeColumnNames = Collections.unmodifiableList(
                applyEntityManagerFactory(
                        emf -> __MappedEntityPersistenceTestUtils.addAllAttributeColumNames(
                                emf,
                                entityClass,
                                new ArrayList<>()
                        )
                )
        );
        logger.log(Level.DEBUG, "attributeColumnNames: {0}", attributeColumnNames);
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

    @DisplayName("assert that all entity columns are mapped")
    @Test
    protected void _Mapped_AllEntityColumnColumnNames() {
        final var table = __MappedEntityTestUtils.getTableAnnotation(entityClass);
        final var catalog = applyEntityManagerFactory(__PersistenceProducerUtils::getDefaultCatalog)
                .orElseGet(table::catalog);
        final var schema = applyEntityManagerFactory(__PersistenceProducerUtils::getDefaultSchema)
                .orElseGet(table::schema);
        final var tableColumnNames = Collections.unmodifiableList(
                applyConnectionInTransactionAndRollback(
                        c -> ___JavaSqlTestUtils.addAllColumnNames(
                                c,
                                catalog,
                                schema,
                                table.name(),
                                new ArrayList<>()
                        )
                )
        );
        assertThat(tableColumnNames)
                .as("table column names for " + entityClass +
                    "; catalog: " + catalog +
                    "; schema: " + schema +
                    "; table: " + table.name()
                )
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
        attributeColumnNames.removeAll(tableColumnNames);
        // -------------------------------------------------------------------------------------------------------------
        assertThat(attributeColumnNames)
                .as("remaining entity column names for " + entityClass)
                .isEmpty();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("select an entity instance of a random index")
    @Test
    protected void selectRandomEntityInstance() {
        final var entityInstance = applyEntityManager(
                em -> ___JakartaPersistenceTestUtils.selectRandom(em, entityClass)
        );
        assumeThat(entityInstance)
                .as("randomly selected entity instance of %s", entityClass)
                .isNotEmpty();
        selectedRandomEntityInstances(entityInstance.get());
    }

    /**
     * Notifies, by the {@link #selectRandomEntityInstance()} method, that the specified entity instance has been
     * selected.
     *
     * @param entityInstance the entity instance.
     * @see #selectRandomEntityInstance()
     */
    protected void selectedRandomEntityInstances(@Nonnull final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        logger.log(Level.DEBUG, "selected entity instance: {0}", entityInstance);
        ___JakartaValidationTestUtils.requireValid(entityInstance);
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
