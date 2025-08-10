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
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntityPersistenceIT<ENTITY extends __MappedEntity<ID>, ID>
        extends __MappedEntityPersistence_<ENTITY, ID> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for testing specified entity class with
     * {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_IT_PU} persistence unit.
     *
     * @param entityClass the entity class to test.
     * @param idClass     an id class of the {@code entityClass}.
     */
    protected __MappedEntityPersistenceIT(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected Collection<String> getTableColumnNames() {
        final var table = __MappedEntityTestUtils.getTableAnnotation(entityClass);
        final var catalog = applyEntityManagerFactory(__PersistenceUnitUtils::getDefaultCatalog)
                .orElseGet(table::catalog);
        final var schema = applyEntityManagerFactory(__PersistenceUnitUtils::getDefaultSchema)
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
        return tableColumnNames;
    }

    protected Collection<String> getEntityColumnNames() {
        final var entityColumnNames = Collections.unmodifiableList(
                applyEntityManagerFactory(
                        emf -> __MappedEntityPersistenceTestUtils.addAllEntityColumNames(
                                emf,
                                entityClass,
                                new ArrayList<>()
                        )
                )
        );
        logger.log(Level.DEBUG, "entity column names: {0}", entityColumnNames);
        assertThat(entityColumnNames)
                .as("entity column names of " + entityClass)
                .isNotEmpty();
        return entityColumnNames;
    }

    /**
     * Checks that all table column names are mapped, and reports to the
     * {@link #_Empty_RemainingTableColumnNames(Collection)} method with remaining table column names.
     *
     * @see #_Empty_RemainingTableColumnNames(Collection)
     */
    @DisplayName("check that all table columns are mapped")
    @Test
    protected void _Mapped_AllTableColumnNames() {
        // ------------------------------------------------------------------------------------------------------- given
        final var tableColumnNames = getTableColumnNames();
        final var attributeColumnNames = Collections.unmodifiableCollection(getEntityColumnNames());
        // -------------------------------------------------------------------------------------------------------- when
        tableColumnNames.removeAll(attributeColumnNames);
        // -------------------------------------------------------------------------------------------------------- then
        _Empty_RemainingTableColumnNames(tableColumnNames);
    }

    /**
     * Asserts that specified remaining table column names, from which all entity column names are removed, is empty.
     *
     * @param remainingTableColumnNames the remaining table column names that all entity column names are removed.
     * @see #_Mapped_AllTableColumnNames()
     */
    protected void _Empty_RemainingTableColumnNames(@Nonnull final Collection<String> remainingTableColumnNames) {
        Objects.requireNonNull(remainingTableColumnNames, "remainingTableColumnNames is null");
        remainingTableColumnNames.forEach(cn -> {
            logger.log(Level.WARNING, "remaining table column name: " + cn);
        });
        assertThat(remainingTableColumnNames)
                .as("remaining table column names")
                .isEmpty();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks that all entity column names are known.
     *
     * @see #_Empty_UnknownEntityColumnNames(Collection)
     */
    @DisplayName("check that all entity columns are known")
    @Test
    protected void _Known_AllEntityColumnColumnNames() {
        // ------------------------------------------------------------------------------------------------------- given
        final var tableColumnNames = Collections.unmodifiableCollection(getTableColumnNames());
        final var entityColumnNames = getEntityColumnNames();
        // -------------------------------------------------------------------------------------------------------- when
        entityColumnNames.removeAll(tableColumnNames);
        // -------------------------------------------------------------------------------------------------------- then
        _Empty_UnknownEntityColumnNames(entityColumnNames);
    }

    /**
     * Asserts that specified remaining entity column names, from which all table column names are removed, is empty.
     *
     * @param unknownEntityColumnNames the remaining entity column names that all table column names are removed.
     */
    protected void _Empty_UnknownEntityColumnNames(@Nonnull final Collection<String> unknownEntityColumnNames) {
        Objects.requireNonNull(unknownEntityColumnNames, "unknownEntityColumnNames is null");
        unknownEntityColumnNames.forEach(cn -> {
            logger.log(Level.WARNING, "remaining entity column name: " + cn);
        });
        assertThat(unknownEntityColumnNames)
                .as("remaining entity column names")
                .isEmpty();
    }

    // ----------------------------------------------------------------------------------------------------- entityClass

    /**
     * Verifies that a randomly selected entity instance is valid.
     *
     * @see ___JakartaPersistenceTestUtils#selectRandom(EntityManager, Class)
     */
    @DisplayName("select an entity instance of a random index")
    @Test
    protected void _Valid_RandomlySelectedEntityInstance() {
        // -------------------------------------------------------------------------------------------------------- when
        final var entityInstance = applyEntityManager(
                em -> ___JakartaPersistenceTestUtils.selectRandom(em, entityClass)
        );
        if (entityInstance.isEmpty()) {
            logger.log(Level.INFO, "no random entity selected; maybe the table is empty?");
            return;
        }
        // -------------------------------------------------------------------------------------------------------- then
        _Valid_RandomlySelectedEntityInstance(entityInstance.get());
    }

    /**
     * Notifies, by the {@link #_Valid_RandomlySelectedEntityInstance()} method, that the specified entity instance has
     * been selected.
     *
     * @param entityInstance the entity instance.
     * @see #_Valid_RandomlySelectedEntityInstance()
     */
    protected void _Valid_RandomlySelectedEntityInstance(@Nonnull final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        logger.log(Level.DEBUG, "selected entity instance: {0}", entityInstance);
        ___JakartaValidationTestUtils.requireValid(entityInstance);
    }

    // -------------------------------------------------------------------------------------- super.entityManagerFactory
    @Override
    final EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.__itPU
    @Inject
    private EntityManagerFactory entityManagerFactory;
}
