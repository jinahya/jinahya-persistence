package com.github.jinahya.persistence.crypto;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

public abstract class __PersistenceCryptoService {

    protected abstract void encrypt(@NotNull Object entityInstance);

    protected abstract void decrypt(@NotNull Object entityInstance);

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __PersistenceCryptoManager manager;
}
