package com.github.jinahya.persistence.encryption.hibernate;

import jakarta.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * What {@code @Encrypted} refuses, each in a persistence unit of its own: an identifier, a version, a natural id. A
 * randomized ciphertext can be none of them, as the column never compares equal to what is bound.
 */
@SuppressWarnings({"java:S101"})
class Refusal_Test {

    private static final String REASON = "which a randomized ciphertext cannot be";

    @DisplayName("@Encrypted on an @Id fails the factory")
    @Test
    void id() {
        assertThatThrownBy(() -> Persistence.createEntityManagerFactory("__encryptionHibernateBadIdPU").close())
                .hasStackTraceContaining("@Id attribute, " + REASON);
    }

    @DisplayName("@Encrypted on a @Version fails the factory")
    @Test
    void version() {
        assertThatThrownBy(() -> Persistence.createEntityManagerFactory("__encryptionHibernateBadVersionPU").close())
                .hasStackTraceContaining("@Version attribute, " + REASON);
    }

    @DisplayName("@Encrypted on a @NaturalId fails the factory")
    @Test
    void naturalId() {
        assertThatThrownBy(() -> Persistence.createEntityManagerFactory("__encryptionHibernateBadNaturalIdPU").close())
                .hasStackTraceContaining("@NaturalId attribute, " + REASON);
    }
}
