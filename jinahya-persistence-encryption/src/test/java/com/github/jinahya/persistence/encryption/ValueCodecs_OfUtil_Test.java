package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ValueCodecs.OfUtil}.
 * <p>
 * Each nested class pins the encoding against {@link ByteBuffer}, which is big-endian, as an independent reference: the
 * encodings are stored data, and a change in one makes every existing value decode differently.
 */
class ValueCodecs_OfUtil_Test {

    /**
     * Returns the bytes the specified writer puts into a big-endian buffer of the specified capacity.
     */
    private static byte[] buffer(final int capacity, final Consumer<? super ByteBuffer> writer) {
        final var buffer = ByteBuffer.allocate(capacity);
        writer.accept(buffer);
        assert !buffer.hasRemaining() : "the expected format does not fill the capacity";
        return buffer.array();
    }

    // ------------------------------------------------------------------------------------------------------------ Date

    @SuppressWarnings("deprecation")
    @DisplayName("Date")
    @Nested
    class DateTest {

        private static Stream<java.util.Date> values() {
            return Stream.of(
                    new java.util.Date(0L),
                    new java.util.Date(-1L),
                    new java.util.Date(Long.MIN_VALUE),
                    new java.util.Date(Long.MAX_VALUE)
            );
        }

        @DisplayName("encodeDate(Date) encodes the documented bytes, and decodeDate(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.util.Date v) {
            final var expected = buffer(OfUtil.DATE_BYTES, bb -> {
                bb.putLong(v.getTime());
            });
            assertThat(OfUtil.encodeDate(v)).isEqualTo(expected);
            assertThat(OfUtil.decodeDate(OfUtil.encodeDate(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final java.util.Date v) {
            final var b = new byte[OfUtil.DATE_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfUtil.encodeDate(b, 1, v)).isEqualTo(1 + OfUtil.DATE_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfUtil.DATE_BYTES);
            assertThat(slice).isEqualTo(OfUtil.encodeDate(v));
            assertThat(OfUtil.decodeDate(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeDate(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfUtil.decodeDate(new byte[OfUtil.DATE_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeDate(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final java.util.Date v) {
            final var b = Arrays.copyOf(OfUtil.encodeDate(v), OfUtil.DATE_BYTES + 1);
            assertThat(OfUtil.decodeDate(b)).isEqualTo(v);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfUtil.DATE_BYTES];
            assertThatThrownBy(() -> OfUtil.encodeDate(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfUtil.decodeDate(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfUtil.encodeDate(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfUtil.decodeDate(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------------ UUID

    @DisplayName("UUID")
    @Nested
    class UuidTest {

        private static Stream<UUID> values() {
            return Stream.of(new UUID(0L, 0L), new UUID(-1L, -1L), new UUID(Long.MIN_VALUE, Long.MAX_VALUE),
                             new UUID(0x0102030405060708L, 0x090A0B0C0D0E0F10L), UUID.randomUUID());
        }

        @DisplayName("encodeUuid(UUID) encodes the most, then the least, significant bits, big-endian, and"
                     + " decodeUuid(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final UUID v) {
            assertThat(OfUtil.encodeUuid(v)).isEqualTo(buffer(
                    OfUtil.UUID_BYTES,
                    b -> b.putLong(v.getMostSignificantBits()).putLong(v.getLeastSignificantBits())));
            assertThat(OfUtil.decodeUuid(OfUtil.encodeUuid(v))).isEqualTo(v);
        }

        @DisplayName("encodeUuid(byte[], int, UUID) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var v = new UUID(0x0102030405060708L, 0x090A0B0C0D0E0F10L);
            final var b = new byte[OfUtil.UUID_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfUtil.encodeUuid(b, 1, v)).isEqualTo(1 + OfUtil.UUID_BYTES);
            assertThat(b).containsExactly(9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 9);
            assertThat(OfUtil.decodeUuid(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeUuid(byte[]) fails on an array shorter than sixteen byte(s)")
        @ValueSource(ints = {0, 15})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfUtil.decodeUuid(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeUuid(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            final var v = UUID.randomUUID();
            final var b = Arrays.copyOf(OfUtil.encodeUuid(v), OfUtil.UUID_BYTES + 1);
            assertThat(OfUtil.decodeUuid(b)).isEqualTo(v);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @ValueSource(ints = {-1, 1, 16})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[OfUtil.UUID_BYTES];
            assertThatThrownBy(() -> OfUtil.encodeUuid(b, i, UUID.randomUUID()))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfUtil.decodeUuid(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the encoders fail on a null value")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfUtil.encodeUuid(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfUtil.encodeUuid(new byte[OfUtil.UUID_BYTES], 0, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
