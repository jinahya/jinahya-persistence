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
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
abstract class __PersistenceUnit_ {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    __PersistenceUnit_() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    abstract EntityManagerFactory getEntityManagerFactory();

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        logger.log(Level.DEBUG, "getEntityManagerFactory(): {0}", getEntityManagerFactory());
        getEntityManagerFactory().getProperties().forEach((k, v) -> {
            logger.log(Level.DEBUG, "entityManagerFactory property: key: {0}, value: {1}", k, v);
        });
        tableCatalog = __PersistenceUnit_TestUtils.getJinahyaTableCatalog(getEntityManagerFactory()).orElseThrow();
        tableSchema = __PersistenceUnit_TestUtils.getJinahyaTableSchema(getEntityManagerFactory()).orElseThrow();
        tableTypes = __PersistenceUnit_TestUtils.getJinahyaTableTypes(getEntityManagerFactory()).orElse(null);
    }

    // https://stackoverflow.com/a/72628439/330457
    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        logger.log(Level.DEBUG, "onStartup{0})", startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(Level.DEBUG, "onShutdown({0})", shutdown);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("print database info")
//    @Test
    @SuppressWarnings({
            "java:S100" // Method names should comply with a naming convention
    })
    protected void printDatabaseInfo__() {
        acceptEntityManager(em -> {
            ___JakartaPersistence_TestUtils.acceptConnectionInTransaction(
                    em,
                    c -> {
                        try {
                            ___JavaSql_TestUtils.printDatabaseInfo(c);
                        } catch (final SQLException sqle) {
                            throw new RuntimeException("failed to print database info", sqle);
                        }
                    }
            );
        });
        acceptEntityManager(em -> {
            ___JakartaPersistence_TestUtils.acceptConnectionInTransaction(
                    em,
                    c -> {
                        try {
                            try (var databaseTableTypes = c.getMetaData().getTableTypes()) {
                                while (databaseTableTypes.next()) {
                                    final var databaseTableType = databaseTableTypes.getString("TABLE_TYPE");
                                    logger.log(Level.INFO, "TABLE_TYPE: {0}", databaseTableType);
                                }
                            }
                        } catch (final SQLException sqle) {
                            throw new RuntimeException("failed to print table types", sqle);
                        }
                    }
            );
        });
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory

    /**
     * Applies an injected instance of {@link EntityManagerFactory} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManagerFactory(Consumer)
     */
    protected final <R> R applyEntityManagerFactory(
            @Nonnull final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getEntityManagerFactory());
    }

    /**
     * Accepts an injected instance of {@link EntityManagerFactory} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManagerFactory(Function)
     */
    protected final void acceptEntityManagerFactory(@Nonnull final Consumer<? super EntityManagerFactory> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerFactory(emf -> {
            consumer.accept(emf);
            return null;
        });
    }

    /**
     * Applies an injected instance of {@link EntityManager} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManager(Consumer)
     */
    protected final <R> R applyEntityManager(@Nonnull final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerFactory(emf -> {
            try (final var entityManager = emf.createEntityManager()) {
                return function.apply(entityManager);
            }
        });
    }

    /**
     * Accepts an injected instance of {@link EntityManager} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManager(Function)
     */
    protected final void acceptEntityManager(@Nonnull final Consumer<? super EntityManager> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the value of {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_CATALOG}
     * property of the injected instance of an {@link EntityManagerFactory}.
     *
     * @return the value of
     *         {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_CATALOG}.
     */
    protected String tableCatalog() {
        return tableCatalog;
    }

    protected String tableSchema() {
        return tableSchema;
    }

    protected String[] tableTypes() {
        return Arrays.copyOf(tableTypes, tableTypes.length);
    }

    // -----------------------------------------------------------------------------------------------------------------q
    private String tableCatalog;

    private String tableSchema;

    private String[] tableTypes;
}
