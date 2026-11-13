package com.github.jinahya.persistence.encryption;

/**
 * A second service implementation, never named in {@code META-INF/services/}: {@code EntityEncryptionServices_Test}
 * names it next to {@link EntityEncryptionServiceImpl} to measure that two named implementations are refused.
 */
public class AnotherEntityEncryptionServiceImpl extends EntityEncryptionServiceImpl {

    public AnotherEntityEncryptionServiceImpl() {
        super();
    }
}
