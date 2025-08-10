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
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntityPersistenceTest<ENTITY extends __MappedEntity<ID>, ID>
        extends __MappedEntityPersistence_<ENTITY, ID> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for testing specified entity class with
     * {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_TEST_PU} persistence unit.
     *
     * @param entityClass the entity class to test.
     * @param idClass     an id class of the {@code entityClass}.
     */
    protected __MappedEntityPersistenceTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // TODO: remove or relocate
    @Disabled
    @DisplayName("__HibernateTestUtils")
    @Nested
    class __HibernateTestUtilsTest {

        @DisplayName("getEntityColumnNames(entityManagerFactory, entityClass)")
        @Test
        @SuppressWarnings({
                "java:S100", // Method names should comply with a naming convention
                "java:S125"  // Sections of code should not be commented out
        })
        void getEntityColumnNames__() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            final var m = ____HibernateTestUtils.class.getDeclaredMethod(
                    "getEntityColumnNames",
                    EntityManagerFactory.class,
                    Class.class
            );
            m.setAccessible(true);
            final var columnNames = (List<String>) m.invoke(null, getEntityManagerFactory(), entityClass);
            for (final var columnName : columnNames) {
                logger.log(System.Logger.Level.DEBUG, "column name: " + columnName);
            }
        }
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory
    @Override
    final EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.__testPU
    @Inject
    private EntityManagerFactory entityManagerFactory;
}
