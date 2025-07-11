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
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

/**
 * A class for testing subclass of {@link __MappedEntity}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
abstract class ___MappedEntityTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance to test the specified entity class.
     *
     * @param entityClass the entity class to test.
     * @param idClass     the id class of the {@code entityClass}.
     * @see #entityClass
     * @see #idClass
     */
    ___MappedEntityTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected ENTITY newEntityInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(entityClass)
                .orElseGet(() -> __JavaLangReflectUtils.newInstanceOf(entityClass));
    }

    protected ENTITY newEntityInstanceSpy() {
        return Mockito.spy(newEntityInstance());
    }

    protected Optional<ENTITY> newRandomizedEntityInstance() {
        return __MappedEntityRandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    protected Optional<ENTITY> newRandomizedEntityInstanceSpy() {
        return Mockito.spy(newRandomizedEntityInstance());
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected ID newIdInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(idClass)
                .orElseGet(() -> __JavaLangReflectUtils.newInstanceOf(idClass));
    }

    protected ID newIdInstanceSpy() {
        return Mockito.spy(newIdInstance());
    }

    protected Optional<ID> newRandomizedIdInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(idClass);
    }

    protected Optional<ID> newRandomizedIdInstanceSpy() {
        return Mockito.spy(newRandomizedIdInstance());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to test.
     */
    protected final Class<ENTITY> entityClass;

    /**
     * The id class of the {@link #entityClass}.
     */
    protected final Class<ID> idClass;
}
