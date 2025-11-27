package com.github.jinahya.persistence.crypto;

import jakarta.persistence.metamodel.Attribute;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __EncryptedAttributeUtils {

    static String getDefaultEncryptedAttributeName(final Attribute<?, ?> attribute) {
        return attribute.getName() + __EncryptedAttributeConstants.DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __EncryptedAttributeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
