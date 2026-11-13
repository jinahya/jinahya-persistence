package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.eclipselink.examples.BadIdEntity;
import com.github.jinahya.persistence.encryption.eclipselink.examples.BadVersionEntity;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * What the encrypted converter refuses when its mapping is initialized, each in a persistence unit of its own: a
 * primary key, the version. A randomized ciphertext can be neither, as the column never compares equal to what is
 * bound. EclipseLink initializes its mappings on the first use of the unit, which is where the failure shows.
 */
@SuppressWarnings({"java:S101"})
class Refusal_Test {

    private static final String REASON = "which a randomized ciphertext cannot be";

    @DisplayName("the converter on an @Id fails the first use of the unit")
    @Test
    void id() {
        assertThatThrownBy(() -> {
            try (var emf = Persistence.createEntityManagerFactory("__encryptionEclipseLinkBadIdPU")) {
                PersistenceTests.apply(emf, em -> em.find(BadIdEntity.class, 1L));
            }
        }).hasStackTraceContaining("on a primary key, " + REASON);
    }

    @DisplayName("the converter on a @Version fails the first use of the unit")
    @Test
    void version() {
        assertThatThrownBy(() -> {
            try (var emf = Persistence.createEntityManagerFactory("__encryptionEclipseLinkBadVersionPU")) {
                PersistenceTests.apply(emf, em -> em.find(BadVersionEntity.class, 1L));
            }
        }).hasStackTraceContaining("on the version, " + REASON);
    }
}
