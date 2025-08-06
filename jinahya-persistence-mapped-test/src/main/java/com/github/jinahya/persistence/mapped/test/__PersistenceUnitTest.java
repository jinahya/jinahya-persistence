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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __PersistenceUnitTest extends __PersistenceUnit_{

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

//    // -----------------------------------------------------------------------------------------------------------------
//    @PostConstruct
//    protected void doOnPostConstruct() {
//        logger.log(System.Logger.Level.DEBUG, "entityManagerFactory: {0}", entityManagerFactory);
//        entityManagerFactory.getProperties().forEach((k, v) -> logger.log(System.Logger.Level.DEBUG, "\t{0}: {1}", k, v));
//        catalog = __PersistenceUnitUtils.getDefaultCatalog(entityManagerFactory).orElseThrow();
//        schema = __PersistenceUnitUtils.getDefaultSchema(entityManagerFactory).orElseThrow();
//        logger.log(System.Logger.Level.DEBUG, "catalog: {0}", catalog);
//        logger.log(System.Logger.Level.DEBUG, "schema: {0}", schema);
//    }
//
//    // https://stackoverflow.com/a/72628439/330457
//    private void onStartup(@Observes final Startup startup) {
//    }

//    // -----------------------------------------------------------------------------------------------------------------
//    @Test
//    public void printDatabaseInfo__() {
//        ___JakartaPersistenceTestUtils.acceptConnection(
//                entityManager,
//                c -> {
//                    try {
//                        ___JavaSqlTestUtils.printDatabaseInfo(c);
//                    } catch (final SQLException sqle) {
//                        throw new RuntimeException("failed to print database info", sqle);
//                    }
//                }
//        );
//    }
//
//    // -----------------------------------------------------------------------------------------------------------------
//    @__PersistenceProducer.Unit__
//    @Inject
//    private EntityManagerFactory entityManagerFactory;
//
//    @Deprecated(forRemoval = true)
//    @__PersistenceProducer.Unit__
//    @Inject
//    private EntityManager entityManager;
//
//    private String catalog;
//
//    private String schema;
}
