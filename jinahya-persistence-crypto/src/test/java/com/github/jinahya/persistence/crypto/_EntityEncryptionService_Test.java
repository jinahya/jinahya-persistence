package com.github.jinahya.persistence.crypto;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class _EntityEncryptionService_Test {

    @Test
    void __0() {
        final var manager = new _EntityEncryptionService();
        final var plain = new byte[0];
        final var encrypted = manager.encrypt("irrelevant", plain);
        assertThat(encrypted)
                .hasSize(
                        _EntityEncryptionService.IV_BYTES
                        + _EntityEncryptionService.KEY_BYTES
                        + _EntityEncryptionService.AAD_BYTES
                        + _EntityEncryptionService.TAG_BYTES
                );
        final var decrypted = manager.decrypt("irrelevant", encrypted);
        assertThat(decrypted).isEqualTo(plain);
    }

    @Test
    void __() {
        final var manager = new _EntityEncryptionService();
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1048576)];
        final var encrypted = manager.encrypt("irrelevant", plain);
        final var decrypted = manager.decrypt("irrelevant", encrypted);
        assertThat(decrypted).isEqualTo(plain);
    }
}
