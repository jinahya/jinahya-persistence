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
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public class __PersistenceProducerIT {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceProducerIT() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        catalog = __PersistenceProducerUtils.getDefaultCatalog(entityManagerFactory).orElseThrow();
        schema = __PersistenceProducerUtils.getDefaultSchema(entityManagerFactory).orElseThrow();
        types = __PersistenceProducerUtils.getDefaultTypes(entityManagerFactory).orElse(null);
    }

    // https://stackoverflow.com/a/72628439/330457
    private void onStartup(@Observes final Startup startup) {
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("print database info")
    @Test
    @SuppressWarnings({
            "java:S100" // Method names should comply with a naming convention
    })
    protected void printDatabaseInfo__() {
        ___JakartaPersistenceTestUtils.acceptConnection(
                entityManager,
                c -> {
                    try {
                        ___JavaSqlTestUtils.printDatabaseInfo(c);
                    } catch (final SQLException sqle) {
                        throw new RuntimeException("failed to print database info", sqle);
                    }
                }
        );
        ___JakartaPersistenceTestUtils.acceptConnection(
                entityManager,
                c -> {
                    try {
                        try (var tableTypes = c.getMetaData().getTableTypes()) {
                            while (tableTypes.next()) {
                                final var tableType = tableTypes.getString("TABLE_TYPE");
                                logger.log(Level.INFO, "TABLE_TYPE: {0}", tableType);
                            }
                        }
                    } catch (final SQLException sqle) {
                        throw new RuntimeException("failed to print table types", sqle);
                    }
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("assert all database tables mapped")
    @Test
    protected void checkAllDatabaseTablesMapped__() {
        final var databaseTableNames = ___JakartaPersistenceTestUtils.applyConnection(
                entityManager,
                c -> {
                    try {
                        return ___JavaSqlTestUtils.addAllTableNames(c, catalog, schema, types, new ArrayList<>());
                    } catch (final SQLException sqle) {
                        throw new RuntimeException("failed to get database table names", sqle);
                    }
                }
        );
        final var entityTableNames = Collections.unmodifiableList(
                ___JakartaPersistenceTestUtils.addAllEntityTableNames(entityManagerFactory, new ArrayList<>())
        );
        logger.log(Level.DEBUG, "all database table names: {0}", databaseTableNames);
        logger.log(Level.DEBUG, "all entity table names: {0}", entityTableNames);
        checkingAllDatabaseTablesMapped(databaseTableNames);
        databaseTableNames.removeAll(entityTableNames);
        databaseTableNames.forEach(tn -> logger.log(Level.WARNING, "remaining database table name: {0}", tn));
    }

    /**
     * Notifies, by the {@link #checkAllDatabaseTablesMapped__()} method, that the specified database table names are to
     * be mapped.
     *
     * @param databaseTableNames the database table names.
     */
    protected void checkingAllDatabaseTablesMapped(@Nonnull final List<String> databaseTableNames) {
        databaseTableNames.removeIf(tn -> {
            return tn.startsWith("HTE_"); // H2 temporary tables
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("check all entity tables known")
    @Test
    protected void checkAllEntityTableNamesKnown__() {
        final var databaseTableNames = Collections.unmodifiableList(
                ___JakartaPersistenceTestUtils.applyConnection(
                        entityManager,
                        c -> {
                            try {
                                return ___JavaSqlTestUtils.addAllTableNames(c, catalog, schema, types,
                                                                            new ArrayList<>());
                            } catch (final SQLException sqle) {
                                throw new RuntimeException("failed to get database table names", sqle);
                            }
                        }
                )
        );
        final var entityTableNames = ___JakartaPersistenceTestUtils.addAllEntityTableNames(
                entityManagerFactory,
                new ArrayList<>()
        );
        logger.log(Level.DEBUG, "all database table names: {0}", databaseTableNames);
        logger.log(Level.DEBUG, "all entity table names: {0}", entityTableNames);
        checkingAllEntityTableNamesKnown(entityTableNames);
        entityTableNames.removeAll(databaseTableNames);
        entityTableNames.forEach(tn -> logger.log(Level.WARNING, "unknown entity table name: {0}", tn));
    }

    /**
     * Notifies, by the {@link #checkAllEntityTableNamesKnown__()}  method, that the specified entity table names are to
     * be known.
     *
     * @param entityTableNames the entity table names.
     */
    protected void checkingAllEntityTableNamesKnown(@Nonnull final List<String> entityTableNames) {
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.Integration__
    @Inject
    @SuppressWarnings({
    })
    private EntityManagerFactory entityManagerFactory;

    @__PersistenceProducer.Integration__
    @Inject
    @SuppressWarnings({
    })
    private EntityManager entityManager;

    private String catalog;

    private String schema;

    private String[] types;
}
