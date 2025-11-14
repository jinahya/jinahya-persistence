package com.github.jinahya.persistence.crypto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface __PersistenceCryptoManager {

    @NotBlank
    String getCryptoIdentifier(@Valid @NotNull Object entityInstance);

    @NotNull
    byte[] encrypt(@NotBlank String cryptoIdentifier, @NotNull byte[] decryptedBytes);

    @NotNull
    byte[] decrypt(@NotBlank String cryptoIdentifier, @NotNull byte[] encryptedBytes);
}
