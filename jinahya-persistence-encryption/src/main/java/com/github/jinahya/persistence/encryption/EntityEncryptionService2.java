package com.github.jinahya.persistence.encryption;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface EntityEncryptionService2 {

    @Nonnull
    @NotNull
    EntityManagerFactory getEntityManagerFactory();

    // -----------------------------------------------------------------------------------------------------------------
    void init(@Nonnull @NotNull EntityEncryptionContext context);

    @Nonnull
    @NotNull
    byte[] encrypt(@Nonnull @NotNull EntityEncryptionContext context, @Nonnull @NotNull byte[] bytes);

    @Nonnull
    @NotNull
    byte[] decrypt(@Nonnull @NotNull EntityEncryptionContext context, @Nonnull @NotNull byte[] bytes);
}
