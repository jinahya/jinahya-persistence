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

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract class for testing the {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_IT_PU}
 * persistence unit.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __PersistenceUnit_Test
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __PersistenceUnit_IT extends __PersistenceUnit_ {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected __PersistenceUnit_IT() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    final EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
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

    /**
     * Returns all database table names.
     *
     * @return all database table names.
     */
    protected Collection<String> getDatabaseTableNames() {
        final var databaseTableNames = applyEntityManager(em -> {
            return ___JakartaPersistence_TestUtils.applyConnectionInTransactionAndRollback(
                    em,
                    c -> {
                        try {
                            return ___JavaSql_TestUtils.addAllTableNames(
                                    c,
                                    tableCatalog(),
                                    tableSchema(),
                                    tableTypes(),
                                    new TreeSet<>()
                            );
                        } catch (final SQLException sqle) {
                            throw new RuntimeException("failed to get database table names", sqle);
                        }
                    }
            );
        });
        logger.log(Level.DEBUG, "all database table names: {0}", databaseTableNames);
        assertThat(databaseTableNames)
                .as("all database table names")
                .isNotEmpty();
        return databaseTableNames;
    }

    /**
     * Returns all persistence table names.
     *
     * @return all persistence table names.
     */
    protected Collection<String> getPersistenceTableNames() {
        final var persistenceTableNames =
                applyEntityManagerFactory(
                        emf -> ___JakartaPersistence_TestUtils.addAllEntityTableNames(
                                emf,
                                new TreeSet<>()
                        )
                );
        logger.log(Level.DEBUG, "all persistence table names: {0}", persistenceTableNames);
        return persistenceTableNames;
    }

    // ------------------------------------------------------------------------------------------------- database tables

    /**
     * Tests that all database tables are mapped to entities.
     *
     * @see #_Mapped_AllDatabaseTableNames(Collection)
     */
    @DisplayName("all database tables are mapped")
    @Test
    void _Mapped_AllDatabaseTableNames() {
        // ------------------------------------------------------------------------------------------------------- given
        final var databaseTableNames = getDatabaseTableNames();
        final var persistenceTableNames = Collections.unmodifiableCollection(getPersistenceTableNames());
        // -------------------------------------------------------------------------------------------------------- when
        databaseTableNames.removeAll(persistenceTableNames);
        logger.log(Level.DEBUG, "remaining database table names: {0}", databaseTableNames);
        // -------------------------------------------------------------------------------------------------------- then
        _Mapped_AllDatabaseTableNames(databaseTableNames);
        assertThat(databaseTableNames)
                .as("remaining database table names")
                .isEmpty();
    }

    /**
     * Notifies that the specified collection of remaining database table names, from which all persistence table names
     * are removed, is verified as empty.
     *
     * @param remainingDatabaseTableNames the collection of remaining database table names from which all persistence
     *                                    table names are removed.
     * @see #_Mapped_AllDatabaseTableNames()
     */
    protected void _Mapped_AllDatabaseTableNames(@Nonnull final Collection<String> remainingDatabaseTableNames) {
        remainingDatabaseTableNames.removeIf(tn -> {
            return tn.startsWith("HTE_"); // H2 temporary tables
        });
        remainingDatabaseTableNames.remove("SEQUENCE");
        remainingDatabaseTableNames.forEach(
                tn -> logger.log(Level.WARNING, "remaining database table name: {0}", tn)
        );
    }

    // ---------------------------------------------------------------------------------------------- persistence tables

    /**
     * Tests that all persistence table names are known.
     *
     * @see #_Known_AllPersistenceTableNames(Collection)
     */
    @DisplayName("check all entity tables are known")
    @Test
    void _Known_AllPersistenceTableNames() {
        // ------------------------------------------------------------------------------------------------------- given
        final var databaseTableNames = Collections.unmodifiableCollection(getDatabaseTableNames());
        final var persistenceTableNames = getPersistenceTableNames();
        // -------------------------------------------------------------------------------------------------------- when
        persistenceTableNames.removeAll(databaseTableNames);
        logger.log(Level.DEBUG, "unknown persistence table names: {0}", persistenceTableNames);
        // -------------------------------------------------------------------------------------------------------- then
        _Known_AllPersistenceTableNames(persistenceTableNames);
        assertThat(persistenceTableNames)
                .as("unknown persistence table names")
                .isEmpty();
    }

    /**
     * Notifies the specified collection of remaining persistence table names, from which all database table names are
     * removed.
     *
     * @param remainingPersistenceTableNames the remaining persistence table names from which all database table names
     *                                       are removed.
     * @see #_Known_AllPersistenceTableNames()
     */
    protected void _Known_AllPersistenceTableNames(@Nonnull final Collection<String> remainingPersistenceTableNames) {
        remainingPersistenceTableNames.forEach(
                tn -> logger.log(Level.WARNING, "unknown persistence table name: {0}", tn)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.__itPU
    @Inject
    private EntityManagerFactory entityManagerFactory;
}
