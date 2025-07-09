package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangUtils;
import jakarta.persistence.EntityManager;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __MappedEntityPersisterUtils {

    static <T> Optional<Class<?>> getPersisterClassOf(final Class<T> type) {
        return Optional.ofNullable(
                __JavaLangUtils.forAnyPostfixes(type, __MappedEntityPersister.class, "Persister", "_Persister")
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <ENTITY extends __MappedEntity<ENTITY, ?>>
    Optional<__MappedEntityPersister<ENTITY>> newPersisterInstanceOf(final Class<ENTITY> type) {
        return getPersisterClassOf(type)
                .map(__JavaLangReflectUtils::newInstanceOf)
                .map(i -> (__MappedEntityPersister<ENTITY>) i);
    }

    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    ENTITY newPersistedInstanceOf(final Class<ENTITY> entityClass, final EntityManager entityManager) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        final ENTITY entityInstance = ___MappedEntityRandomizerUtils.newRandomizedInstanceOf(entityClass)
                .orElseThrow(() -> new RuntimeException("failed to get random instance of " + entityClass));
        return newPersisterInstanceOf(entityClass)
                .map(p -> p.persist(entityManager, entityInstance))
                .orElseThrow(() -> new RuntimeException("failed to get persister instance of " + entityClass));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityPersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
