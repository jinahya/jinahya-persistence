package com.github.jinahya.persistence.crypto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.Bytes_l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.Characters_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.byte_1;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.chars_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.serializable_;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@Slf4j
class __EncryptionServiceUtils_Test {

    private static byte[] randomBytes(final int minLengthInclusive, final int maxLengthInclusive) {
        assert minLengthInclusive >= 0;
        assert maxLengthInclusive >= minLengthInclusive;
        final var v = new byte[ThreadLocalRandom.current().nextInt(minLengthInclusive, maxLengthInclusive + 1)];
        ThreadLocalRandom.current().nextBytes(v);
        return v;
    }

    // -----------------------------------------------------------------------------------------------------------------
    __EncryptionServiceUtils_Test() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @ValueSource(bytes = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE})
    @ParameterizedTest
    void byte__(final byte v) {
        final var encoded = byte_1(v);
        assertThat(encoded).hasSize(Byte.BYTES);
    }

    // -----------------------------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------------------- Byte[]
    @Test
    void Bytes_l__() {
        final var p = randomBytes(1, 128);
        final var v = new Byte[p.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = p[i];
        }
        // an expected value which the call under test cannot reach
        final var expected = v.clone();

        final var b = Bytes_l(v);

        assertThat(b).as("the encoded bytes").isEqualTo(p);
        assertThat(v).as("the argument must not be modified").isEqualTo(expected);
    }

    @DisplayName("an odd number of bytes is rejected, not silently truncated")
    @Test
    void chars_2l__odd() {
        assertThatThrownBy(() -> chars_2l(new byte[]{1, 2, 3}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("odd number of bytes");
    }

    @DisplayName("Characters_2l encodes two bytes per character, without modifying its argument")
    @Test
    void Characters_2l__() {
        final var v = new Character[]{'j', 'i', 'n', '\u00e4', '\uD55C', '\uFFFF', '\u0080'};
        final var expected = v.clone();

        final var b = Characters_2l(v);

        assertThat(b).as("two bytes per character").hasSize(v.length << 1);
        assertThat(v).as("the argument must not be modified").isEqualTo(expected);
    }

    /**
     * A value with a nested object graph, for verifying that the filter pins the root type without rejecting what the
     * root holds.
     */
    static class Holder implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        Holder(final String name, final BigDecimal amount) {
            this.name = name;
            this.amount = amount;
        }

        final String name;

        final BigDecimal amount;
    }

    @DisplayName("serializable_ rejects a graph whose root is not of the expected type")
    @Test
    void serializable__wrongRoot() {
        final var encoded = serializable_(new Holder("jane", BigDecimal.ONE));
        // the cast enforces the type; the filter only caps resources, so that serialization proxies still work
        assertThatThrownBy(() -> serializable_(encoded, BigDecimal.class))
                .isInstanceOf(ClassCastException.class);
    }

    @DisplayName("serializable_ refuses to WRITE what it could never read back")
    @Test
    void serializable__oversizedIsRejectedOnWrite() {
        // a value larger than the reader's stream cap used to encrypt and store happily, and then fail
        // every subsequent read -- with the plaintext already cleared, so the row was unrecoverable
        final var oversized = new byte[(1 << 20) + 1024];
        assertThatThrownBy(() -> serializable_((java.io.Serializable) oversized))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too large");
    }

}
