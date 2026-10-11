package com.github.jinahya.persistence.crypto;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class EntityEncryptionServiceImpl_Test {

    @Test
    void __0() {
        final var manager = new EntityEncryptionServiceImpl();
        final var plain = new byte[0];
        final var encrypted = manager.encrypt("irrelevant", plain);
        assertThat(encrypted)
                .hasSize(
                        EntityEncryptionServiceImpl.IV_BYTES
                        + EntityEncryptionServiceImpl.KEY_BYTES
                        + EntityEncryptionServiceImpl.AAD_BYTES
                        + EntityEncryptionServiceImpl.TAG_BYTES
                );
        final var decrypted = manager.decrypt("irrelevant", encrypted);
        assertThat(decrypted).isEqualTo(plain);
    }

    @Test
    void __() {
        final var manager = new EntityEncryptionServiceImpl();
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1048576)];
        final var encrypted = manager.encrypt("irrelevant", plain);
        final var decrypted = manager.decrypt("irrelevant", encrypted);
        assertThat(decrypted).isEqualTo(plain);
    }
}
