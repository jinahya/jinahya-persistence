package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.util.JinahyaAttributeUtils;
import jakarta.persistence.metamodel.Attribute;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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
     * A validated pair of a plaintext and the attribute which holds its ciphertext.
     * <p>
     * The plaintext is either a persistent attribute (Mode A, {@code decrypted}), or a transient field (Mode B,
     * {@code plaintextField}); exactly one of the two is set.
     *
     * @param decrypted      the attribute holding the plaintext, in Mode A; {@code null} in Mode B.
     * @param plaintextField the transient field holding the plaintext, in Mode B; {@code null} in Mode A.
     * @param encrypted      the attribute holding the ciphertext.
     * @param javaType       the declared java type of the plaintext, resolved against the concrete class.
     */
    record Pair(@Nullable Attribute<?, ?> decrypted, java.lang.reflect.@Nullable Field plaintextField,
                Attribute<?, ?> encrypted, Class<?> javaType) {

        Pair {
            assert (decrypted == null) != (plaintextField == null);
        }

        /**
         * Returns whether the plaintext is a transient field: Mode B.
         */
        boolean isTransient() {
            return plaintextField != null;
        }

        /**
         * Returns the name of the plaintext attribute, or field.
         */
        String name() {
            return plaintextField != null ? plaintextField.getName() : Objects.requireNonNull(decrypted).getName();
        }

        @Nullable Object getPlaintext(final Object target) {
            if (plaintextField != null) {
                try {
                    return plaintextField.get(target);
                } catch (final IllegalAccessException iae) {
                    throw new RuntimeException("failed to read " + plaintextField, iae);
                }
            }
            return JinahyaAttributeUtils.getAttributeValue(target, Objects.requireNonNull(decrypted));
        }

        Runnable setPlaintext(final Object target, final @Nullable Object value) {
            final var field = plaintextField;
            if (field != null) {
                return () -> {
                    try {
                        field.set(target, value);
                    } catch (final IllegalAccessException iae) {
                        throw new RuntimeException("failed to write " + field, iae);
                    }
                };
            }
            final var attribute = Objects.requireNonNull(decrypted);
            return () -> JinahyaAttributeUtils.setAttributeValue(target, attribute, value);
        }

        byte @Nullable [] getCiphertext(final Object target) {
            return (byte[]) JinahyaAttributeUtils.getAttributeValue(target, encrypted);
        }

        Runnable setCiphertext(final Object target, final byte @Nullable [] value) {
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
