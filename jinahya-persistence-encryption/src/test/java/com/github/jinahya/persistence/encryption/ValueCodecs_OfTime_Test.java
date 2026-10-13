package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ValueCodecs.OfTime}.
 * <p>
 * Each nested class pins the encoding against {@link ByteBuffer}, which is big-endian, as an independent reference:
 * the encodings are stored data, and a change in one makes every existing value decode differently.
 */
class ValueCodecs_OfTime_Test {

    /**
     * Returns the bytes the specified writer puts into a big-endian buffer of the specified capacity.
     */
    private static byte[] buffer(final int capacity, final Consumer<? super ByteBuffer> writer) {
        final var buffer = ByteBuffer.allocate(capacity);
        writer.accept(buffer);
        assert !buffer.hasRemaining() : "the expected format does not fill the capacity";
        return buffer.array();
    }

    // ------------------------------------------------------------------------------------------------------- LocalDate

    @DisplayName("LocalDate")
    @Nested
    class LocalDateTest {

        private static Stream<LocalDate> values() {
            return Stream.of(
                    LocalDate.MIN,
                    LocalDate.MAX,
                    LocalDate.EPOCH,
                    LocalDate.of(2024, 2, 29)
            );
        }

        @DisplayName("encodeLocalDate(LocalDate) encodes the documented bytes, and decodeLocalDate(byte[]) reads them"
                     + " back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final LocalDate v) {
            final var expected = buffer(OfTime.LOCAL_DATE_BYTES, bb -> {
                bb.putLong(v.toEpochDay());
            });
            assertThat(OfTime.encodeLocalDate(v)).isEqualTo(expected);
            assertThat(OfTime.decodeLocalDate(OfTime.encodeLocalDate(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final LocalDate v) {
            final var b = new byte[OfTime.LOCAL_DATE_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeLocalDate(b, 1, v)).isEqualTo(1 + OfTime.LOCAL_DATE_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.LOCAL_DATE_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeLocalDate(v));
            assertThat(OfTime.decodeLocalDate(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeLocalDate(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeLocalDate(new byte[OfTime.LOCAL_DATE_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeLocalDate(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final LocalDate v) {
            final var b = Arrays.copyOf(OfTime.encodeLocalDate(v), OfTime.LOCAL_DATE_BYTES + 1);
            assertThat(OfTime.decodeLocalDate(b)).isEqualTo(v);
        }

        @DisplayName("decodeLocalDate(byte[]) fails on an epoch day beyond LocalDate.MAX")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.LOCAL_DATE_BYTES, bb -> {
                bb.putLong(LocalDate.MAX.toEpochDay() + 1L);
            });
            assertThatThrownBy(() -> OfTime.decodeLocalDate(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.LOCAL_DATE_BYTES];
            assertThatThrownBy(() -> OfTime.encodeLocalDate(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeLocalDate(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeLocalDate(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeLocalDate(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------- LocalTime

    @DisplayName("LocalTime")
    @Nested
    class LocalTimeTest {

        private static Stream<LocalTime> values() {
            return Stream.of(
                    LocalTime.MIN,
                    LocalTime.MAX,
                    LocalTime.NOON,
                    LocalTime.of(1, 2, 3, 4)
            );
        }

        @DisplayName("encodeLocalTime(LocalTime) encodes the documented bytes, and decodeLocalTime(byte[]) reads them"
                     + " back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final LocalTime v) {
            final var expected = buffer(OfTime.LOCAL_TIME_BYTES, bb -> {
                bb.putLong(v.toNanoOfDay());
            });
            assertThat(OfTime.encodeLocalTime(v)).isEqualTo(expected);
            assertThat(OfTime.decodeLocalTime(OfTime.encodeLocalTime(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final LocalTime v) {
            final var b = new byte[OfTime.LOCAL_TIME_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeLocalTime(b, 1, v)).isEqualTo(1 + OfTime.LOCAL_TIME_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.LOCAL_TIME_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeLocalTime(v));
            assertThat(OfTime.decodeLocalTime(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeLocalTime(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeLocalTime(new byte[OfTime.LOCAL_TIME_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeLocalTime(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final LocalTime v) {
            final var b = Arrays.copyOf(OfTime.encodeLocalTime(v), OfTime.LOCAL_TIME_BYTES + 1);
            assertThat(OfTime.decodeLocalTime(b)).isEqualTo(v);
        }

        @DisplayName("decodeLocalTime(byte[]) fails on a nano-of-day of a whole day")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.LOCAL_TIME_BYTES, bb -> {
                bb.putLong(LocalTime.MAX.toNanoOfDay() + 1L);
            });
            assertThatThrownBy(() -> OfTime.decodeLocalTime(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.LOCAL_TIME_BYTES];
            assertThatThrownBy(() -> OfTime.encodeLocalTime(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeLocalTime(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeLocalTime(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeLocalTime(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // --------------------------------------------------------------------------------------------------- LocalDateTime

    @DisplayName("LocalDateTime")
    @Nested
    class LocalDateTimeTest {

        private static Stream<LocalDateTime> values() {
            return Stream.of(
                    LocalDateTime.MIN,
                    LocalDateTime.MAX,
                    LocalDateTime.of(2024, 2, 29, 23, 59, 59, 999_999_999)
            );
        }

        @DisplayName("encodeLocalDateTime(LocalDateTime) encodes the documented bytes, and decodeLocalDateTime(byte[])"
                     + " reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final LocalDateTime v) {
            final var expected = buffer(OfTime.LOCAL_DATE_TIME_BYTES, bb -> {
                bb.putLong(v.toLocalDate().toEpochDay()).putLong(v.toLocalTime().toNanoOfDay());
            });
            assertThat(OfTime.encodeLocalDateTime(v)).isEqualTo(expected);
            assertThat(OfTime.decodeLocalDateTime(OfTime.encodeLocalDateTime(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final LocalDateTime v) {
            final var b = new byte[OfTime.LOCAL_DATE_TIME_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeLocalDateTime(b, 1, v)).isEqualTo(1 + OfTime.LOCAL_DATE_TIME_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.LOCAL_DATE_TIME_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeLocalDateTime(v));
            assertThat(OfTime.decodeLocalDateTime(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeLocalDateTime(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeLocalDateTime(new byte[OfTime.LOCAL_DATE_TIME_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeLocalDateTime(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final LocalDateTime v) {
            final var b = Arrays.copyOf(OfTime.encodeLocalDateTime(v), OfTime.LOCAL_DATE_TIME_BYTES + 1);
            assertThat(OfTime.decodeLocalDateTime(b)).isEqualTo(v);
        }

        @DisplayName("decodeLocalDateTime(byte[]) fails on a time beyond a day")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.LOCAL_DATE_TIME_BYTES, bb -> {
                bb.putLong(0L).putLong(LocalTime.MAX.toNanoOfDay() + 1L);
            });
            assertThatThrownBy(() -> OfTime.decodeLocalDateTime(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.LOCAL_DATE_TIME_BYTES];
            assertThatThrownBy(() -> OfTime.encodeLocalDateTime(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeLocalDateTime(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeLocalDateTime(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeLocalDateTime(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------ ZoneOffset

    @DisplayName("ZoneOffset")
    @Nested
    class ZoneOffsetTest {

        private static Stream<ZoneOffset> values() {
            return Stream.of(
                    ZoneOffset.MIN,
                    ZoneOffset.MAX,
                    ZoneOffset.UTC,
                    ZoneOffset.ofHoursMinutes(5, 30)
            );
        }

        @DisplayName("encodeZoneOffset(ZoneOffset) encodes the documented bytes, and decodeZoneOffset(byte[]) reads"
                     + " them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final ZoneOffset v) {
            final var expected = buffer(OfTime.ZONE_OFFSET_BYTES, bb -> {
                bb.putInt(v.getTotalSeconds());
            });
            assertThat(OfTime.encodeZoneOffset(v)).isEqualTo(expected);
            assertThat(OfTime.decodeZoneOffset(OfTime.encodeZoneOffset(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final ZoneOffset v) {
            final var b = new byte[OfTime.ZONE_OFFSET_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeZoneOffset(b, 1, v)).isEqualTo(1 + OfTime.ZONE_OFFSET_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.ZONE_OFFSET_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeZoneOffset(v));
            assertThat(OfTime.decodeZoneOffset(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeZoneOffset(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeZoneOffset(new byte[OfTime.ZONE_OFFSET_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeZoneOffset(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final ZoneOffset v) {
            final var b = Arrays.copyOf(OfTime.encodeZoneOffset(v), OfTime.ZONE_OFFSET_BYTES + 1);
            assertThat(OfTime.decodeZoneOffset(b)).isEqualTo(v);
        }

        @DisplayName("decodeZoneOffset(byte[]) fails on an offset beyond +18:00")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.ZONE_OFFSET_BYTES, bb -> {
                bb.putInt(ZoneOffset.MAX.getTotalSeconds() + 1);
            });
            assertThatThrownBy(() -> OfTime.decodeZoneOffset(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.ZONE_OFFSET_BYTES];
            assertThatThrownBy(() -> OfTime.encodeZoneOffset(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeZoneOffset(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeZoneOffset(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeZoneOffset(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------ OffsetTime

    @DisplayName("OffsetTime")
    @Nested
    class OffsetTimeTest {

        private static Stream<OffsetTime> values() {
            return Stream.of(
                    OffsetTime.MIN,
                    OffsetTime.MAX,
                    OffsetTime.of(LocalTime.NOON, ZoneOffset.ofHours(9))
            );
        }

        @DisplayName("encodeOffsetTime(OffsetTime) encodes the documented bytes, and decodeOffsetTime(byte[]) reads"
                     + " them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final OffsetTime v) {
            final var expected = buffer(OfTime.OFFSET_TIME_BYTES, bb -> {
                bb.putLong(v.toLocalTime().toNanoOfDay()).putInt(v.getOffset().getTotalSeconds());
            });
            assertThat(OfTime.encodeOffsetTime(v)).isEqualTo(expected);
            assertThat(OfTime.decodeOffsetTime(OfTime.encodeOffsetTime(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final OffsetTime v) {
            final var b = new byte[OfTime.OFFSET_TIME_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeOffsetTime(b, 1, v)).isEqualTo(1 + OfTime.OFFSET_TIME_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.OFFSET_TIME_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeOffsetTime(v));
            assertThat(OfTime.decodeOffsetTime(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeOffsetTime(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeOffsetTime(new byte[OfTime.OFFSET_TIME_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeOffsetTime(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final OffsetTime v) {
            final var b = Arrays.copyOf(OfTime.encodeOffsetTime(v), OfTime.OFFSET_TIME_BYTES + 1);
            assertThat(OfTime.decodeOffsetTime(b)).isEqualTo(v);
        }

        @DisplayName("decodeOffsetTime(byte[]) fails on an offset beyond -18:00")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.OFFSET_TIME_BYTES, bb -> {
                bb.putLong(0L).putInt(ZoneOffset.MIN.getTotalSeconds() - 1);
            });
            assertThatThrownBy(() -> OfTime.decodeOffsetTime(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.OFFSET_TIME_BYTES];
            assertThatThrownBy(() -> OfTime.encodeOffsetTime(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeOffsetTime(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeOffsetTime(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeOffsetTime(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // -------------------------------------------------------------------------------------------------- OffsetDateTime

    @DisplayName("OffsetDateTime")
    @Nested
    class OffsetDateTimeTest {

        private static Stream<OffsetDateTime> values() {
            return Stream.of(
                    OffsetDateTime.MIN,
                    OffsetDateTime.MAX,
                    OffsetDateTime.of(LocalDateTime.of(2024, 2, 29, 23, 59, 59, 999_999_999), ZoneOffset.ofHours(-3))
            );
        }

        @DisplayName("encodeOffsetDateTime(OffsetDateTime) encodes the documented bytes, and"
                     + " decodeOffsetDateTime(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final OffsetDateTime v) {
            final var expected = buffer(OfTime.OFFSET_DATE_TIME_BYTES, bb -> {
                bb
                        .putLong(v.toLocalDate().toEpochDay())
                        .putLong(v.toLocalTime().toNanoOfDay())
                        .putInt(v.getOffset().getTotalSeconds());
            });
            assertThat(OfTime.encodeOffsetDateTime(v)).isEqualTo(expected);
            assertThat(OfTime.decodeOffsetDateTime(OfTime.encodeOffsetDateTime(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final OffsetDateTime v) {
            final var b = new byte[OfTime.OFFSET_DATE_TIME_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeOffsetDateTime(b, 1, v)).isEqualTo(1 + OfTime.OFFSET_DATE_TIME_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.OFFSET_DATE_TIME_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeOffsetDateTime(v));
            assertThat(OfTime.decodeOffsetDateTime(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeOffsetDateTime(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeOffsetDateTime(new byte[OfTime.OFFSET_DATE_TIME_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeOffsetDateTime(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final OffsetDateTime v) {
            final var b = Arrays.copyOf(OfTime.encodeOffsetDateTime(v), OfTime.OFFSET_DATE_TIME_BYTES + 1);
            assertThat(OfTime.decodeOffsetDateTime(b)).isEqualTo(v);
        }

        @DisplayName("decodeOffsetDateTime(byte[]) fails on an offset beyond +18:00")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.OFFSET_DATE_TIME_BYTES, bb -> {
                bb.putLong(0L).putLong(0L).putInt(ZoneOffset.MAX.getTotalSeconds() + 1);
            });
            assertThatThrownBy(() -> OfTime.decodeOffsetDateTime(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.OFFSET_DATE_TIME_BYTES];
            assertThatThrownBy(() -> OfTime.encodeOffsetDateTime(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeOffsetDateTime(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeOffsetDateTime(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeOffsetDateTime(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // --------------------------------------------------------------------------------------------------------- Instant

    @DisplayName("Instant")
    @Nested
    class InstantTest {

        private static Stream<Instant> values() {
            return Stream.of(
                    Instant.MIN,
                    Instant.MAX,
                    Instant.EPOCH,
                    Instant.ofEpochSecond(-1L, 999_999_999L)
            );
        }

        @DisplayName("encodeInstant(Instant) encodes the documented bytes, and decodeInstant(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final Instant v) {
            final var expected = buffer(OfTime.INSTANT_BYTES, bb -> {
                bb.putLong(v.getEpochSecond()).putInt(v.getNano());
            });
            assertThat(OfTime.encodeInstant(v)).isEqualTo(expected);
            assertThat(OfTime.decodeInstant(OfTime.encodeInstant(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final Instant v) {
            final var b = new byte[OfTime.INSTANT_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeInstant(b, 1, v)).isEqualTo(1 + OfTime.INSTANT_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.INSTANT_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeInstant(v));
            assertThat(OfTime.decodeInstant(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeInstant(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeInstant(new byte[OfTime.INSTANT_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeInstant(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final Instant v) {
            final var b = Arrays.copyOf(OfTime.encodeInstant(v), OfTime.INSTANT_BYTES + 1);
            assertThat(OfTime.decodeInstant(b)).isEqualTo(v);
        }

        @DisplayName("decodeInstant(byte[]) fails on a nano-of-second of a whole second, rather than carrying it into"
                     + " the second")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.INSTANT_BYTES, bb -> {
                bb.putLong(0L).putInt(1_000_000_000);
            });
            assertThatThrownBy(() -> OfTime.decodeInstant(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("decodeInstant(byte[]) fails on a negative nano-of-second, rather than borrowing it from the"
                     + " second")
        @Test
        void __outOfRange1() {
            final var b = buffer(OfTime.INSTANT_BYTES, bb -> {
                bb.putLong(0L).putInt(-1);
            });
            assertThatThrownBy(() -> OfTime.decodeInstant(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("decodeInstant(byte[]) fails on an epoch second beyond Instant.MAX")
        @Test
        void __outOfRange2() {
            final var b = buffer(OfTime.INSTANT_BYTES, bb -> {
                bb.putLong(Instant.MAX.getEpochSecond() + 1L).putInt(0);
            });
            assertThatThrownBy(() -> OfTime.decodeInstant(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.INSTANT_BYTES];
            assertThatThrownBy(() -> OfTime.encodeInstant(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeInstant(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeInstant(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeInstant(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------------ Year

    @DisplayName("Year")
    @Nested
    class YearTest {

        private static Stream<Year> values() {
            return Stream.of(
                    Year.of(Year.MIN_VALUE),
                    Year.of(Year.MAX_VALUE),
                    Year.of(0),
                    Year.of(2024)
            );
        }

        @DisplayName("encodeYear(Year) encodes the documented bytes, and decodeYear(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final Year v) {
            final var expected = buffer(OfTime.YEAR_BYTES, bb -> {
                bb.putInt(v.getValue());
            });
            assertThat(OfTime.encodeYear(v)).isEqualTo(expected);
            assertThat(OfTime.decodeYear(OfTime.encodeYear(v))).isEqualTo(v);
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @MethodSource("values")
        @ParameterizedTest
        void __index(final Year v) {
            final var b = new byte[OfTime.YEAR_BYTES + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfTime.encodeYear(b, 1, v)).isEqualTo(1 + OfTime.YEAR_BYTES);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            final var slice = Arrays.copyOfRange(b, 1, 1 + OfTime.YEAR_BYTES);
            assertThat(slice).isEqualTo(OfTime.encodeYear(v));
            assertThat(OfTime.decodeYear(b, 1)).isEqualTo(v);
        }

        @DisplayName("decodeYear(byte[]) fails on an array shorter than the value")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfTime.decodeYear(new byte[OfTime.YEAR_BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeYear(byte[]) reads the beginning of a longer array, and leaves the rest")
        @MethodSource("values")
        @ParameterizedTest
        void __longer(final Year v) {
            final var b = Arrays.copyOf(OfTime.encodeYear(v), OfTime.YEAR_BYTES + 1);
            assertThat(OfTime.decodeYear(b)).isEqualTo(v);
        }

        @DisplayName("decodeYear(byte[]) fails on a value beyond Year.MAX_VALUE")
        @Test
        void __outOfRange0() {
            final var b = buffer(OfTime.YEAR_BYTES, bb -> {
                bb.putInt(Year.MAX_VALUE + 1);
            });
            assertThatThrownBy(() -> OfTime.decodeYear(b))
                    .isInstanceOf(DateTimeException.class);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @Test
        void __outOfBounds() {
            final var v = values().findFirst().orElseThrow();
            final var b = new byte[OfTime.YEAR_BYTES];
            assertThatThrownBy(() -> OfTime.encodeYear(b, 1, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfTime.decodeYear(b, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfTime.encodeYear(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfTime.decodeYear(null)).isInstanceOf(NullPointerException.class);
        }
    }
}
