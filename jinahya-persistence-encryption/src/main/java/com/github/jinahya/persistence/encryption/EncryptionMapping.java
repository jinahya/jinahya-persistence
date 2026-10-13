package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.metamodel.util.JinahyaAttributeUtils;
import jakarta.persistence.metamodel.Attribute;

import java.util.List;

/**
 * The validated encryption mapping of a managed type: what {@link EntityEncryptor} builds for an entity class on first
 * use, caches, and walks to encrypt and decrypt its instances.
 *
 * @param pairs     the encrypted pairs to transform.
 * @param embeddeds the embedded attributes to descend into, each with the mapping built for its own path.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
record EncryptionMapping(List<Pair> pairs, List<Embedded> embeddeds) {

    /**
     * Returns whether this mapping, or that of any embeddable it reaches, has an encrypted pair.
     *
     * @return {@code true} when there is anything to encrypt.
     */
    boolean hasPairs() {
        if (!pairs.isEmpty()) {
            return true;
        }
        for (final var embedded : embeddeds) {
            if (embedded.mapping().hasPairs()) {
                return true;
            }
        }
        return false;
    }

    /**
     * The column rules of one attribute.
     *
     * @param insertable whether the provider may write the column in an {@code INSERT}.
     * @param updatable  whether the provider may write the column in an {@code UPDATE}.
     * @param nullable   whether the column accepts {@code null}.
     * @param source     where the facts came from, for diagnostics; never a value.
     */
    record ColumnRules(boolean insertable, boolean updatable, boolean nullable, String source) {

    }

    /**
     * A validated pair of the attribute which holds a plaintext and the attribute which holds its ciphertext.
     *
     * @param decrypted the attribute holding the plaintext.
     * @param encrypted the attribute holding the ciphertext.
     * @param javaType  the declared java type of the plaintext, resolved against the concrete class.
     * @param nulled    whether the plaintext is nulled before an {@code UPDATE}: it is not primitive, and its column is
     *                  mapped nullable.
     */
    record Pair(Attribute<?, ?> decrypted, Attribute<?, ?> encrypted, Class<?> javaType, boolean nulled) {

        /**
         * Returns the name of the plaintext attribute.
         */
        String name() {
            return decrypted.getName();
        }

        @org.jspecify.annotations.Nullable Object getPlaintext(final Object target) {
            return JinahyaAttributeUtils.getAttributeValue(target, decrypted);
        }

        Runnable setPlaintext(final Object target, final @org.jspecify.annotations.Nullable Object value) {
            return () -> JinahyaAttributeUtils.setAttributeValue(target, decrypted, value);
        }

        byte @org.jspecify.annotations.Nullable [] getCiphertext(final Object target) {
            return (byte[]) JinahyaAttributeUtils.getAttributeValue(target, encrypted);
        }

        Runnable setCiphertext(final Object target, final byte @org.jspecify.annotations.Nullable [] value) {
            return () -> JinahyaAttributeUtils.setAttributeValue(target, encrypted, value);
        }
    }

    /**
     * An embedded attribute, with the mapping validated for the path it was reached by.
     *
     * @param attribute the embedded attribute.
     * @param mapping   the validated mapping of the embeddable, in this attribute's override scope.
     */
    record Embedded(Attribute<?, ?> attribute, EncryptionMapping mapping) {

    }
}
