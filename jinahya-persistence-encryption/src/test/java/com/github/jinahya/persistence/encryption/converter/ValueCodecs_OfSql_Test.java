package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfSql;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ValueCodecs.OfSql}.
 * <p>
 * Each nested class pins the encoding against {@link ByteBuffer}, which is big-endian, as an independent reference: the
 * encodings are stored data, and a change in one makes every existing value decode differently.
 */
@SuppressWarnings("deprecation")
class ValueCodecs_OfSql_Test {

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

    @DisplayName("Date")
    @Nested
    class DateTest {

        private static Stream<java.sql.Date> values() {
            return Stream.of(
                    new java.sql.Date(0L),
                    new java.sql.Date(-1L),
                    new java.sql.Date(Long.MIN_VALUE),
                    new java.sql.Date(Long.MAX_VALUE),
                    java.sql.Date.valueOf("2024-02-29")
            );
        }

        @DisplayName("encodeDate(Date) encodes the documented bytes, and decodeDate(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.sql.Date v) {
            final var expected = buffer(OfSql.DATE_BYTES, bb -> {
                bb.putLong(v.getTime());
            });
            assertThat(OfSql.encodeDate(v)).isEqualTo(expected);
            assertThat(OfSql.decodeDate(OfSql.encodeDate(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final java.sql.Date v) {
            final var b = new byte[OfSql.DATE_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfSql.encodeDate(b, 1, v)).isEqualTo(1 + OfSql.DATE_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfSql.DATE_BYTES);
            assertThat(slice).isEqualTo(OfSql.encodeDate(v));
            assertThat(OfSql.decodeDate(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeDate(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfSql.decodeDate(new byte[OfSql.DATE_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeDate(byte[]) fails on an array longer than the value")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final java.sql.Date v) {
            final var b = Arrays.copyOf(OfSql.encodeDate(v), OfSql.DATE_BYTES + 1);
            assertThatThrownBy(() -> OfSql.decodeDate(b)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfSql.DATE_BYTES];
            assertThatThrownBy(() -> OfSql.encodeDate(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfSql.decodeDate(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfSql.encodeDate(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfSql.decodeDate(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------------ Time

    @DisplayName("Time")
    @Nested
    class TimeTest {

        private static Stream<java.sql.Time> values() {
            return Stream.of(
                    new java.sql.Time(0L),
                    new java.sql.Time(-1L),
                    java.sql.Time.valueOf("23:59:59")
            );
        }

        @DisplayName("encodeTime(Time) encodes the documented bytes, and decodeTime(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.sql.Time v) {
            final var expected = buffer(OfSql.TIME_BYTES, bb -> {
                bb.putLong(v.getTime());
            });
            assertThat(OfSql.encodeTime(v)).isEqualTo(expected);
            assertThat(OfSql.decodeTime(OfSql.encodeTime(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final java.sql.Time v) {
            final var b = new byte[OfSql.TIME_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfSql.encodeTime(b, 1, v)).isEqualTo(1 + OfSql.TIME_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfSql.TIME_BYTES);
            assertThat(slice).isEqualTo(OfSql.encodeTime(v));
            assertThat(OfSql.decodeTime(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeTime(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfSql.decodeTime(new byte[OfSql.TIME_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeTime(byte[]) fails on an array longer than the value")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final java.sql.Time v) {
            final var b = Arrays.copyOf(OfSql.encodeTime(v), OfSql.TIME_BYTES + 1);
            assertThatThrownBy(() -> OfSql.decodeTime(b)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfSql.TIME_BYTES];
            assertThatThrownBy(() -> OfSql.encodeTime(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfSql.decodeTime(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfSql.encodeTime(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfSql.decodeTime(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------- Timestamp

    @DisplayName("Timestamp")
    @Nested
    class TimestampTest {

        private static Stream<java.sql.Timestamp> values() {
            return Stream.of(
                    java.sql.Timestamp.from(Instant.EPOCH),
                    java.sql.Timestamp.from(Instant.ofEpochSecond(-1L, 123_456_789L)),
                    java.sql.Timestamp.valueOf("2024-02-29 23:59:59.999999999")
            );
        }

        @DisplayName("encodeTimestamp(Timestamp) encodes the documented bytes, and decodeTimestamp(byte[]) reads them"
                     + " back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.sql.Timestamp v) {
            final var expected = buffer(OfSql.TIMESTAMP_BYTES, bb -> {
                bb.putLong(v.toInstant().getEpochSecond()).putInt(v.toInstant().getNano());
            });
            assertThat(OfSql.encodeTimestamp(v)).isEqualTo(expected);
            assertThat(OfSql.decodeTimestamp(OfSql.encodeTimestamp(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final java.sql.Timestamp v) {
            final var b = new byte[OfSql.TIMESTAMP_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfSql.encodeTimestamp(b, 1, v)).isEqualTo(1 + OfSql.TIMESTAMP_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfSql.TIMESTAMP_BYTES);
            assertThat(slice).isEqualTo(OfSql.encodeTimestamp(v));
            assertThat(OfSql.decodeTimestamp(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeTimestamp(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfSql.decodeTimestamp(new byte[OfSql.TIMESTAMP_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeTimestamp(byte[]) fails on an array longer than the value")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final java.sql.Timestamp v) {
            final var b = Arrays.copyOf(OfSql.encodeTimestamp(v), OfSql.TIMESTAMP_BYTES + 1);
            assertThatThrownBy(() -> OfSql.decodeTimestamp(b)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeTimestamp(byte[]) fails on a nano-of-second of a whole second")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfSql.TIMESTAMP_BYTES, bb -> {
                bb.putLong(0L).putInt(1_000_000_000);
            });
            assertThatThrownBy(() -> OfSql.decodeTimestamp(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("decodeTimestamp(byte[]) fails on an instant beyond the range of a Timestamp")
        @Test
        void __outOfRange1() {
            final var b = buffer(OfSql.TIMESTAMP_BYTES, bb -> {
                bb.putLong(Instant.MAX.getEpochSecond()).putInt(0);
            });
            assertThatThrownBy(() -> OfSql.decodeTimestamp(b))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfSql.TIMESTAMP_BYTES];
            assertThatThrownBy(() -> OfSql.encodeTimestamp(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfSql.decodeTimestamp(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfSql.encodeTimestamp(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfSql.decodeTimestamp(null)).isInstanceOf(NullPointerException.class);
        }
    }
}
