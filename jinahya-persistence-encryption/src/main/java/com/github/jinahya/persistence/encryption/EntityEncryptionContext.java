package com.github.jinahya.persistence.encryption;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

public interface EntityEncryptionContext {

    @Nonnull
    @NotNull
    Object getEntityInstance();
}
