package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangUtils;
import jakarta.persistence.EntityManager;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class ___PersisterUtils {

    static <T> Optional<Class<?>> getPersisterClassOf(final Class<T> entityClass) {
        return Optional.ofNullable(
                __JavaLangUtils.forAnyPostfixes(entityClass, __MappedEntityPersister.class, "Persister", "_Persister")
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Persister<T>> newPersisterInstanceOf(final Class<T> entityClass) {
        return getPersisterClassOf(entityClass)
                .map(__JavaLangReflectUtils::newInstanceOf)
                .map(i -> (___Persister<T>) i);
    }

    public static <T> T newPersistedInstanceOf(final EntityManager entityManager, final Class<T> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        final T entityInstance = ___RandomizerUtils.newRandomizedInstanceOf(entityClass)
                .orElseThrow(() -> new RuntimeException("failed to get random instance of " + entityClass));
        return newPersisterInstanceOf(entityClass)
                .map(p -> p.persist(entityManager, entityInstance))
                .orElseThrow(() -> new RuntimeException("failed to get persister instance of " + entityClass));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___PersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
