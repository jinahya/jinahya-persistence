package com.github.jinahya.persistence.crypto;

import jakarta.persistence.metamodel.Attribute;

/**
 * A utility class for {@link __EncryptedAttribute}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __EncryptedAttributeUtils {

    /**
     * Returns the default name of the attribute which holds the encrypted bytes of the specified attribute.
     *
     * @param attribute the attribute holding the decrypted value.
     * @return the {@code attribute}'s name suffixed with
     *         {@link __EncryptedAttributeConstants#DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX}.
     */
    static String getDefaultEncryptedAttributeName(final Attribute<?, ?> attribute) {
        return attribute.getName() + __EncryptedAttributeConstants.DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __EncryptedAttributeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
