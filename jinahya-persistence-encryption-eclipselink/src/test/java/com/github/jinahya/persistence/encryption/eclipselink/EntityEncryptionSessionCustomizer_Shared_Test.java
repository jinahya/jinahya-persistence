package com.github.jinahya.persistence.encryption.eclipselink;

import org.junit.jupiter.api.DisplayName;

@DisplayName("shared cache ALL")
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class EntityEncryptionSessionCustomizer_Shared_Test extends EntityEncryptionSessionCustomizer_Test {

    EntityEncryptionSessionCustomizer_Shared_Test() {
        super("__encryptionEclipseLinkSharedPU");
    }
}
