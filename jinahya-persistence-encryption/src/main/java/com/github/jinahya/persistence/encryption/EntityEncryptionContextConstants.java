package com.github.jinahya.persistence.encryption;

final class EntityEncryptionContextConstants {

    static final String DEFAULT_NAME = Long.toString(System.nanoTime());

    private EntityEncryptionContextConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
