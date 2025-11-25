package com.github.jinahya.persistence.crypto;

import jakarta.persistence.metamodel.Attribute;

final class __EncryptedAttributeUtils {

    public static String getDefaultEncryptedAttributeName(final Attribute<?, ?> attribute) {
        return attribute.getName() + __EncryptedAttributeConstants.DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __EncryptedAttributeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
