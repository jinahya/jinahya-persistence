package com.github.jinahya.persistence.mapped.tests;

import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
final class ___MappedEntityPersistenceTestUtils {

    static Optional<String> getDefaultCatalog(final EntityManagerFactory entityManagerFactory) {
        return Optional.ofNullable(
                (String) entityManagerFactory.getProperties().get(
                        ___MappedEntityPersistenceConstants.PERSISTENCE_UNIT_PROPERTY_DEFAULT_CATALOG
                )
        );
    }

    static Optional<String> getDefaultSchema(final EntityManagerFactory entityManagerFactory) {
        return Optional.ofNullable(
                (String) entityManagerFactory.getProperties().get(
                        ___MappedEntityPersistenceConstants.PERSISTENCE_UNIT_PROPERTY_DEFAULT_SCHEMA
                )
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___MappedEntityPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
