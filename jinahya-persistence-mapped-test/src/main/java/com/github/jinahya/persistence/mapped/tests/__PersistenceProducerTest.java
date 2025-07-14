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

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;

/**
 * An abstract provider class for EntityManagerFactory and EntityManager.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class __PersistenceProducerTest {

    @Test
    @SuppressWarnings({
            "java:S100" // Method names should comply with a naming convention
    })
    public void printDatabaseInfo__() {
        ___JakartaPersistenceTestUtils.applyConnection(
                entityManager,
                c -> {
                    try {
                        ___JavaSqlTestUtils.printDatabaseInfo(c);
                    } catch (final SQLException sqle) {
                        throw new RuntimeException("failed to print database info", sqle);
                    }
                    return null;
                },
                true
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.Unit__
    @Inject
    private EntityManager entityManager;
}
