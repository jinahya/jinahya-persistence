package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;

public abstract class __PersistenceCryptoService {

    protected abstract void encrypt(@Nonnull Object entityInstance);

    protected abstract void decrypt(@Nonnull Object entityInstance);
}
