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
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
public abstract class __MappedEntity_PersistenceIT<ENTITY extends __MappedEntity<ID>, ID>
        extends __MappedEntity_Persistence_<ENTITY, ID> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for testing specified entity class with
     * {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_IT_PU} persistence unit.
     *
     * @param entityClass the entity class to test.
     * @param idClass     an id class of the {@code entityClass}.
     */
    protected __MappedEntity_PersistenceIT(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    @PostConstruct
    protected void doOnPostConstruct() {
        super.doOnPostConstruct();
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        super.onStartup(startup);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Asserts no modification to the schema.
     */
    @DisplayName("no modification to the schema")
    @BeforeEach
    protected final void assertNoSchemaModification() {
        acceptEntityManagerFactory(__PersistenceProducer_TestUtils::assertSchemagenDatabaseActionNone);
        acceptEntityManagerFactory(__PersistenceProducer_TestUtils::assertEclipselinkDdlGenerationNone);
        acceptEntityManagerFactory(__PersistenceProducer_TestUtils::assertHibernateHbm2ddlAutoNone);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected Collection<String> getTableColumnNames() {
        final var table = __MappedEntity_TestUtils.getTableAnnotation(entityClass);
        final var catalog = applyEntityManagerFactory(__PersistenceUnit_TestUtils::getDefaultCatalog)
                .orElseGet(table::catalog);
        final var schema = applyEntityManagerFactory(__PersistenceUnit_TestUtils::getDefaultSchema)
                .orElseGet(table::schema);
        final var tableColumnNames = applyConnectionInTransactionAndRollback(
                c -> ___JavaSql_TestUtils.addAllColumnNames(
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
        final var entityColumnNames = applyEntityManagerFactory(
                emf -> ___JakartaPersistence_TestUtils.addAllEntityColumNames(
                        emf,
                        entityClass,
                        new ArrayList<>()
                )
        );
        logger.log(Level.DEBUG, "entity column names: {0}", entityColumnNames);
        assertThat(entityColumnNames)
                .as("entity column names of " + entityClass)
                .isNotEmpty();
        return entityColumnNames;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks that all table column names are mapped, and reports to the
     * {@link #_Mapped_AllTableColumnNames(Collection)} method with remaining table column names.
     *
     * @see #_Mapped_AllTableColumnNames(Collection)
     */
    @DisplayName("check that all table columns are mapped")
    @Test
    protected void _Mapped_AllTableColumnNames() {
        // ------------------------------------------------------------------------------------------------------- given
        final var tableColumnNames = getTableColumnNames();
        final var attributeColumnNames = Collections.unmodifiableCollection(getEntityColumnNames());
        // -------------------------------------------------------------------------------------------------------- when
        tableColumnNames.removeAll(attributeColumnNames);
        _Mapped_AllTableColumnNames(tableColumnNames);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(tableColumnNames)
                .as("remaining table column names")
                .isEmpty();
    }

    /**
     * Notifies specified remaining table column names, from which all entity column names are removed.
     *
     * @param tableColumnNames the remaining table column names that all entity column names are removed.
     * @see #_Mapped_AllTableColumnNames()
     */
    protected void _Mapped_AllTableColumnNames(@Nonnull final Collection<String> tableColumnNames) {
        Objects.requireNonNull(tableColumnNames, "tableColumnNames is null");
        tableColumnNames.forEach(cn -> {
            logger.log(Level.WARNING, "remaining table column name: " + cn);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks that all entity column names are known.
     *
     * @see #_Empty_AllEntityColumnNames(Collection)
     */
    @DisplayName("check that all entity columns are known")
    @Test
    protected void _Known_AllEntityColumnNames() {
        // ------------------------------------------------------------------------------------------------------- given
        final var tableColumnNames = Collections.unmodifiableCollection(getTableColumnNames());
        final var entityColumnNames = getEntityColumnNames();
        // -------------------------------------------------------------------------------------------------------- when
        entityColumnNames.removeAll(tableColumnNames);
        _Empty_AllEntityColumnNames(entityColumnNames);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(entityColumnNames)
                .as("remaining entity column names")
                .isEmpty();
    }

    /**
     * Notifies specified remaining entity column names, from which all table column names are removed.
     *
     * @param remainingEntityColumnNames the remaining entity column names that all table column names are removed.
     */
    protected void _Empty_AllEntityColumnNames(@Nonnull final Collection<String> remainingEntityColumnNames) {
        Objects.requireNonNull(remainingEntityColumnNames, "remainingEntityColumnNames is null");
        remainingEntityColumnNames.forEach(cn -> {
            logger.log(Level.WARNING, "remaining entity column name: " + cn);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * All values of {@code @Column(nullable)} should match table column's nullability.
     */
    protected void _MatchToCorrespondingTableColumnNullable_AllEntityColumnNullable() {
        final var tableColumnNamesAndIsNullables = applyConnectionInTransactionAndRollback(
                c -> ___JavaSql_TestUtils.getColumnNameAndIsNullable(
                        c,
                        getTableCatalog(),
                        getTableSchema(),
                        getTableName()
                )
        );
        final var managedType = applyEntityManagerFactory(em -> {
            return em.getMetamodel().managedType(entityClass);
        });
        managedType.getAttributes().forEach(a -> {
            final var member = a.getJavaMember();
            final Column column;
            if (member instanceof Field field) {
                column = field.getAnnotation(Column.class);
            } else if (member instanceof Method method) {
                column = method.getAnnotation(Column.class);
            } else {
                throw new RuntimeException("unknown member type: " + member);
            }
            if (column == null) {
                logger.log(Level.WARNING, "no @Column annotation for " + member);
                return;
            }
            final var name = column.name();
            final var nullable = column.nullable();
            final var isNullable = tableColumnNamesAndIsNullables.get(name);
            assertThat(nullable)
                    .as("Column#nullable of %s maps to %s(%s) ", member, name, isNullable)
                    .isEqualTo(isNullable);
        });
    }

    // ----------------------------------------------------------------------------------------------------- entityClass

    /**
     * Verifies that a randomly selected entity instance is valid.
     *
     * @see ___JakartaPersistence_TestUtils#selectRandom(EntityManager, Class)
     */
    @DisplayName("select an entity instance of a random index")
    @Test
    protected void _Valid_RandomlySelectedEntityInstance() {
        acceptEntityManager(em -> {
            // ---------------------------------------------------------------------------------------------------- when
            final var selected = ___JakartaPersistence_TestUtils.selectRandom(em, entityClass);
            if (selected.isEmpty()) {
                logger.log(Level.INFO, "no random entity selected; maybe the table is empty?");
                return;
            }
            // ---------------------------------------------------------------------------------------------------- then
            _Valid_RandomlySelectedEntityInstance(selected.get());
        });
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
        logger.log(Level.DEBUG, "randomly selected entity instance: {0}", entityInstance);
        ___JakartaValidation_TestUtils.requireValid(entityInstance);
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
