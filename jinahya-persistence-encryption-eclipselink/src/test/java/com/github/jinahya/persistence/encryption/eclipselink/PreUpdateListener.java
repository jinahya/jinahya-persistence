package com.github.jinahya.persistence.encryption.eclipselink;

import jakarta.persistence.PreUpdate;

/**
 * An entity listener whose {@code @PreUpdate} assigns {@link SecretEntity#name} (#107).
 */
public class PreUpdateListener {

    @PreUpdate
    void preUpdate(final Object entity) {
        if (entity instanceof SecretEntity secret && secret.nameOnListenerPreUpdate != null) {
            secret.name = secret.nameOnListenerPreUpdate;
        }
    }
}
