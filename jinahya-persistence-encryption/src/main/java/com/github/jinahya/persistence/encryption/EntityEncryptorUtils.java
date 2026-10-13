package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.EncryptionMapping.Pair;
import com.github.jinahya.persistence.encryption.ValueCodecs.OfLang;
import com.github.jinahya.persistence.encryption.ValueCodecs.OfMath;
import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;
import com.github.jinahya.persistence.encryption.ValueCodecs.OfSql;
import com.github.jinahya.persistence.encryption.ValueCodecs.OfTime;
import com.github.jinahya.persistence.encryption.ValueCodecs.OfUtil;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.UUID;

/**
 * A utility class for turning the Jakarta Persistence basic types into bytes, and back.
 * <p>
 * {@link #encode(Pair, Object)} and {@link #decode(Pair, byte[])} pick the codec by an attribute's current declared
 * type; nothing about the type is stored with the bytes, so a value is always read as the type it is declared now. The
 * codecs are those of {@link ValueCodecs}, but for {@code Byte[]} and {@code Character[]}, which are here. A stored
 * payload is decoded only when it is exactly as long as its codec reads, where {@link ValueCodecs} allows bytes beyond
 * it. The encodings are big-endian and self-contained, so that a value can be reconstructed from its bytes without
 * consulting the database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptor
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
final class EntityEncryptorUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the specified payload after checking that it is exactly as long as a fixed-width codec reads.
     *
     * @param b      the payload.
     * @param length the width of the codec, in bytes.
     * @return the {@code b}.
     * @throws IllegalArgumentException when the {@code b} is shorter, or longer, than the {@code length}.
     * @implNote Checked unconditionally, not with {@code assert}: a payload which is too long would otherwise
     *         be read silently, as its first {@code length} bytes.
     */
    private static byte[] exactly(final byte[] b, final int length) {
        if (b.length != length) {
            throw new IllegalArgumentException(
                    "a payload of " + b.length + " byte(s); this codec reads exactly " + length);
        }
        return b;
    }

    // ---------------------------------------------------------------------------------------------------------- Byte[]

    /**
     * Returns an array of bytes representing the specified {@code Byte[]} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Byte[]} for basic attributes in favor of
     *         {@code byte[]}.
     */
    @Deprecated
    static byte[] Bytes_l(final Byte[] v) {
        final var p = new byte[v.length];
        for (int i = 0; i < p.length; i++) {
            if (v[i] == null) {
                throw new IllegalArgumentException(
                        "a null element at " + i + "; a Byte[] is encoded one byte per element");
            }
            p[i] = v[i];
        }
        return p;
    }

    /**
     * Returns the {@code Byte[]} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Byte[]} for basic attributes in favor of
     *         {@code byte[]}.
     */
    @Deprecated
    static Byte[] Bytes_l(final byte[] b) {
        final var v = new Byte[b.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = b[i];
        }
        return v;
    }

    // ----------------------------------------------------------------------------------------------------- Character[]

    /**
     * Returns an array of bytes representing the specified {@code Character[]} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] Characters_2l(final Character[] v) {
        final var p = new char[v.length];
        for (int i = 0; i < p.length; i++) {
            if (v[i] == null) {
                throw new IllegalArgumentException(
                        "a null element at " + i + "; a Character[] is encoded one char per element");
            }
            p[i] = v[i];
        }
        return OfPrimitive.encodeChars(p);
    }

    /**
     * Returns the {@code Character[]} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static Character[] Characters_2l(final byte[] b) {
        final var p = OfPrimitive.decodeChars(b);
        final var v = new Character[p.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = p[i];
        }
        return v;
    }

    // ------------------------------------------------------------------------------------------------------- supports

    /**
     * Tells whether the specified declared java type of an attribute is supported; whether
     * {@link #encode(Pair, Object)} and {@link #decode(Pair, byte[])} have a codec for it.
     *
     * @param javaType the declared java type of an attribute.
     * @return {@code true} when the {@code javaType} is supported; {@code false} otherwise.
     */
    static boolean supports(final Class<?> javaType) {
        return SUPPORTED.contains(javaType)
               || java.util.Date.class.isAssignableFrom(javaType) // the java.sql types, and its other subclasses
               || javaType.isEnum();
    }

    private static final java.util.Set<Class<?>> SUPPORTED = java.util.Set.of(
            boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class, int.class, Integer.class,
            long.class, Long.class, char.class, Character.class, float.class, Float.class, double.class, Double.class,
            String.class, UUID.class, BigInteger.class, BigDecimal.class,
            LocalDate.class, LocalTime.class, LocalDateTime.class, OffsetTime.class, OffsetDateTime.class,
            Instant.class, Year.class,
            byte[].class, Byte[].class, char[].class, Character[].class
    );

    // ------------------------------------------------------------------------------------------------- encode / decode

    /**
     * Returns the specified plaintext value of the specified pair, encoded by its declared java type.
     *
     * @param pair           the pair.
     * @param decryptedValue the plaintext value.
     * @return the encoding of the {@code decryptedValue}; never the instance's own array.
     */
    static byte[] encode(final Pair pair, final Object decryptedValue) {
        final var javaType = pair.javaType();
        // The DECLARED type decides the encoding, exactly as it decides the decoding below. Dispatching on
        // the runtime class here instead let the two ladders pick different codecs for one attribute -- a
        // java.util.Date holding a java.sql.Timestamp was written as epoch seconds and read back as epoch
        // millis, silently. Keep this chain in the same order as the one in decode(...).
        if (!javaType.isInstance(decryptedValue)) {
            throw new RuntimeException(
                    "the value is not an instance of the attribute's declared java type" +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName() +
                    "; value type: " + decryptedValue.getClass().getName()
            );
        }
        try {
            if (javaType == Boolean.class) {
                return OfPrimitive.encodeBoolean((Boolean) decryptedValue);
            } else if (javaType == Byte.class) {
                return OfPrimitive.encodeByte((Byte) decryptedValue);
            } else if (javaType == Short.class) {
                return OfPrimitive.encodeShort((Short) decryptedValue);
            } else if (javaType == Integer.class) {
                return OfPrimitive.encodeInt((Integer) decryptedValue);
            } else if (javaType == Long.class) {
                return OfPrimitive.encodeLong((Long) decryptedValue);
            } else if (javaType == Character.class) {
                return OfPrimitive.encodeChar((Character) decryptedValue);
            } else if (javaType == Float.class) {
                return OfPrimitive.encodeFloat((Float) decryptedValue);
            } else if (javaType == Double.class) {
                return OfPrimitive.encodeDouble((Double) decryptedValue);
            } else if (javaType == String.class) {
                return OfLang.encodeString((String) decryptedValue);
            } else if (javaType == UUID.class) {
                return OfUtil.encodeUuid((UUID) decryptedValue);
            } else if (javaType == BigInteger.class) {
                return OfMath.encodeBigInteger((BigInteger) decryptedValue);
            } else if (javaType == BigDecimal.class) {
                return OfMath.encodeBigDecimal((BigDecimal) decryptedValue);
            } else if (javaType == LocalDate.class) {
                return OfTime.encodeLocalDate((LocalDate) decryptedValue);
            } else if (javaType == LocalTime.class) {
                return OfTime.encodeLocalTime((LocalTime) decryptedValue);
            } else if (javaType == LocalDateTime.class) {
                return OfTime.encodeLocalDateTime((LocalDateTime) decryptedValue);
            } else if (javaType == OffsetTime.class) {
                return OfTime.encodeOffsetTime((OffsetTime) decryptedValue);
            } else if (javaType == OffsetDateTime.class) {
                return OfTime.encodeOffsetDateTime((OffsetDateTime) decryptedValue);
            } else if (javaType == Instant.class) {
                return OfTime.encodeInstant((Instant) decryptedValue);
            } else if (javaType == Year.class) {
                return OfTime.encodeYear((Year) decryptedValue);
            } else if (javaType == java.sql.Timestamp.class) { // before java.util.Date; keeps the nanos
                return OfSql.encodeTimestamp((java.sql.Timestamp) decryptedValue);
            } else if (javaType == java.sql.Date.class) {      // before java.util.Date
                return OfSql.encodeDate((java.sql.Date) decryptedValue);
            } else if (javaType == java.sql.Time.class) {      // before java.util.Date
                return OfSql.encodeTime((java.sql.Time) decryptedValue);
            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
                // one branch feeds both of decrypt's generic Date branches -- the exact `== java.util.Date` one
                // and the reflective (long) constructor one -- because both read long_8 millis
                return OfUtil.encodeDate((java.util.Date) decryptedValue);
            } else if (javaType == byte[].class) {
                // a copy: the service must not be handed the instance's own array
                return ((byte[]) decryptedValue).clone();
            } else if (javaType == Byte[].class) {
                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
                return Bytes_l((Byte[]) decryptedValue);
            } else if (javaType == char[].class) {
                return OfPrimitive.encodeChars((char[]) decryptedValue);
            } else if (javaType == Character[].class) {
                logger.log(System.Logger.Level.WARNING, "Character[] is not encouraged; use char[]");
                return Characters_2l((Character[]) decryptedValue);
            } else if (javaType.isEnum()) {
                return OfLang.encodeEnum((Enum<?>) decryptedValue);
            }
        } catch (final Exception e) {
            // a value the codec refuses, or fails, to encode: a string with an unpaired surrogate, a Byte[] or a
            // Character[] with a null element, ... -- reported here while the caller still holds it. The cause's
            // message is not repeated, as it may not be ours; the cause carries it. The message never carries the
            // value.
            throw new RuntimeException(
                    "cannot encode the value" +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName(),
                    e
            );
        }
        throw new RuntimeException("unsupported java type: " + javaType);
    }

    /**
     * Returns the plaintext value of the specified pair, decoded from the specified bytes by its declared java type.
     *
     * @param pair        the pair.
     * @param decryptedBytes the decrypted bytes.
     * @return the decoded value.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static Object decode(final Pair pair, final byte[] decryptedBytes) {
        // the declared java type, for the same reason as in encode(...): the two ladders must agree on the type
        final var javaType = pair.javaType();
        try {
            if (javaType == boolean.class || javaType == Boolean.class) {
                return OfPrimitive.decodeBoolean(exactly(decryptedBytes, Byte.BYTES), 0);
            } else if (javaType == byte.class || javaType == Byte.class) {
                return OfPrimitive.decodeByte(exactly(decryptedBytes, Byte.BYTES), 0);
            } else if (javaType == short.class || javaType == Short.class) {
                return OfPrimitive.decodeShort(exactly(decryptedBytes, Short.BYTES), 0);
            } else if (javaType == int.class || javaType == Integer.class) {
                return OfPrimitive.decodeInt(exactly(decryptedBytes, Integer.BYTES), 0);
            } else if (javaType == long.class || javaType == Long.class) {
                return OfPrimitive.decodeLong(exactly(decryptedBytes, Long.BYTES), 0);
            } else if (javaType == char.class || javaType == Character.class) {
                return OfPrimitive.decodeChar(exactly(decryptedBytes, Character.BYTES), 0);
            } else if (javaType == float.class || javaType == Float.class) {
                return OfPrimitive.decodeFloat(exactly(decryptedBytes, Float.BYTES), 0);
            } else if (javaType == double.class || javaType == Double.class) {
                return OfPrimitive.decodeDouble(exactly(decryptedBytes, Double.BYTES), 0);
            } else if (javaType == String.class) {
                return OfLang.decodeString(decryptedBytes);
            } else if (javaType == UUID.class) {
                return OfUtil.decodeUuid(exactly(decryptedBytes, OfUtil.UUID_BYTES), 0);
            } else if (javaType == BigInteger.class) {
                return OfMath.decodeBigInteger(decryptedBytes);
            } else if (javaType == BigDecimal.class) {
                return OfMath.decodeBigDecimal(decryptedBytes);
            } else if (javaType == LocalDate.class) {
                return OfTime.decodeLocalDate(exactly(decryptedBytes, OfTime.LOCAL_DATE_BYTES), 0);
            } else if (javaType == LocalTime.class) {
                return OfTime.decodeLocalTime(exactly(decryptedBytes, OfTime.LOCAL_TIME_BYTES), 0);
            } else if (javaType == LocalDateTime.class) {
                return OfTime.decodeLocalDateTime(exactly(decryptedBytes, OfTime.LOCAL_DATE_TIME_BYTES), 0);
            } else if (javaType == OffsetTime.class) {
                return OfTime.decodeOffsetTime(exactly(decryptedBytes, OfTime.OFFSET_TIME_BYTES), 0);
            } else if (javaType == OffsetDateTime.class) {
                return OfTime.decodeOffsetDateTime(exactly(decryptedBytes, OfTime.OFFSET_DATE_TIME_BYTES), 0);
            } else if (javaType == Instant.class) {
                return OfTime.decodeInstant(exactly(decryptedBytes, OfTime.INSTANT_BYTES), 0);
            } else if (javaType == Year.class) {
                return OfTime.decodeYear(exactly(decryptedBytes, OfTime.YEAR_BYTES), 0);
            } else if (javaType == java.sql.Timestamp.class) { // before java.util.Date; keeps the nanos
                return OfSql.decodeTimestamp(exactly(decryptedBytes, OfSql.TIMESTAMP_BYTES), 0);
            } else if (javaType == java.sql.Date.class) {      // before java.util.Date
                return OfSql.decodeDate(exactly(decryptedBytes, OfSql.DATE_BYTES), 0);
            } else if (javaType == java.sql.Time.class) {      // before java.util.Date
                return OfSql.decodeTime(exactly(decryptedBytes, OfSql.TIME_BYTES), 0);
            } else if (javaType == java.util.Date.class) {
                return OfUtil.decodeDate(exactly(decryptedBytes, OfUtil.DATE_BYTES), 0);
            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
                final var time = OfPrimitive.decodeLong(exactly(decryptedBytes, Long.BYTES), 0);
                return javaType.getConstructor(long.class).newInstance(time);
            } else if (javaType == byte[].class) {
                return decryptedBytes;
            } else if (javaType == Byte[].class) {
                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
                return Bytes_l(decryptedBytes);
            } else if (javaType == char[].class) {
                return OfPrimitive.decodeChars(decryptedBytes);
            } else if (javaType == Character[].class) {
                logger.log(System.Logger.Level.WARNING, "Character[] is not encouraged; use char[]");
                return Characters_2l(decryptedBytes);
            } else if (javaType.isEnum()) {
                return OfLang.decodeEnum(decryptedBytes, (Class) javaType);
            }
        } catch (final Exception e) {
            // failed to decode: its message may not be ours, so it isn't repeated; the cause carries it
            throw new RuntimeException(
                    "cannot reconstruct the value from the decrypted bytes" +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName() +
                    "; decrypted bytes: " + decryptedBytes.length,
                    e
            );
        }
        throw new RuntimeException("unsupported java type: " + javaType);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private EntityEncryptorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
