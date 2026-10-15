package com.github.jinahya.persistence.encryption.eclipselink;

import org.junit.jupiter.api.DisplayName;

@DisplayName("shared cache NONE")
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class EntityEncryptionSessionCustomizer_Isolated_Test extends EntityEncryptionSessionCustomizer_Test {

    EntityEncryptionSessionCustomizer_Isolated_Test() {
        super("__encryptionEclipseLinkIsolatedPU");
    }
}
