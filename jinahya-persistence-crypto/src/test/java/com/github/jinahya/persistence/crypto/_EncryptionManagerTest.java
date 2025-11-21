package com.github.jinahya.persistence.crypto;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class _EncryptionManagerTest {

    @Test
    void __0() {
        final var manager = new _EncryptionManager();
        final var plain = new byte[0];
        final var encrypted = manager.encrypt("irrelevant", plain);
        assertThat(encrypted).hasSize(12 + 16);
        final var decrypted = manager.decrypt("irrelevant", encrypted);
        assertThat(decrypted).isEqualTo(plain);
    }

    @Test
    void __() {
        final var manager = new _EncryptionManager();
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1048576)];
        final var encrypted = manager.encrypt("irrelevant", plain);
        assertThat(encrypted).hasSize(12 + 16 + plain.length);
        final var decrypted = manager.decrypt("irrelevant", encrypted);
        assertThat(decrypted).isEqualTo(plain);
    }
}
