package com.github.jinahya.persistence.crypto;

/**
 * Constants for {@link __EncryptedAttribute}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __EncryptedAttributeConstants {

    /**
     * The postfix appended to a decrypted attribute's name to derive the name of the attribute holding the encrypted
     * bytes. The value is {@value #DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX}.
     */
    public static final String DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX = "_encrypted__";

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Creates a new instance, which is not allowed.
     */
    private __EncryptedAttributeConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
