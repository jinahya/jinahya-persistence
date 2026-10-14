package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.AllTypesEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Round trips one encrypted attribute of each supported java type, primitives included, through the persistence
 * provider and its database: persisted, reloaded, updated and reloaded again, the row never holding a plaintext.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "deprecation", // java.util.Date, java.sql.*, Byte[] and Character[]
        "java:S101"    // Class names should comply with a naming convention
})
class AllTypes_Test {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__encryptionPU");
        LifecycleListenerEntity.SERVICE = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        LifecycleListenerEntity.SERVICE = null;
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        try (final var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                final var result = function.apply(entityManager);
                transaction.commit();
                return result;
            } catch (final RuntimeException re) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw re;
            }
        }
    }

    private static AllTypesEntity populated() {
        final var entity = new AllTypesEntity();
        entity.primitiveBoolean = true;
        entity.wrapperBoolean = Boolean.TRUE;
        entity.primitiveByte = (byte) -7;
        entity.wrapperByte = (byte) 7;
        entity.primitiveShort = (short) -1234;
        entity.wrapperShort = (short) 1234;
        entity.primitiveInt = -37;
        entity.wrapperInt = 37;
        entity.primitiveLong = -1_234_567_890_123L;
        entity.wrapperLong = 1_234_567_890_123L;
        entity.primitiveChar = 'K';
        entity.wrapperChar = '\uD55C';
        entity.primitiveFloat = -3.5f;
        entity.wrapperFloat = 3.5f;
        entity.primitiveDouble = -2.25d;
        entity.wrapperDouble = 2.25d;
        entity.string = "Jane Roe \uD55C\uAE00";
        entity.uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        entity.bigInteger = new BigInteger("-123456789012345678901234567890");
        entity.bigDecimal = new BigDecimal("12345.6789");
        entity.localDate = LocalDate.of(1988, 11, 3);
        entity.localTime = LocalTime.of(3, 4, 5, 123_456_789);
        entity.localDateTime = LocalDateTime.of(2026, 1, 2, 3, 4, 5, 123_456_789);
        entity.offsetTime = OffsetTime.of(10, 11, 12, 13, ZoneOffset.ofHours(9));
        entity.offsetDateTime = OffsetDateTime.of(2026, 1, 2, 3, 4, 5, 6, ZoneOffset.ofHoursMinutes(-5, -30));
        entity.instant = Instant.ofEpochSecond(1_700_000_000L, 123_456_789);
        entity.year = Year.of(2026);
        entity.utilDate = new java.util.Date(1_700_000_000_123L);
        entity.sqlDate = java.sql.Date.valueOf("2026-01-02");
        entity.sqlTime = java.sql.Time.valueOf("03:04:05");
        entity.sqlTimestamp = java.sql.Timestamp.valueOf("2026-01-02 03:04:05.123456789");
        entity.bytes = new byte[]{0, 1, -1, 127, -128};
        entity.wrapperBytes = new Byte[]{0, 1, -1, 127, -128};
        entity.chars = new char[]{'a', '\uD55C', '\0'};
        entity.wrapperChars = new Character[]{'a', '\uD55C', '\0'};
        entity.grade = AllTypesEntity.Grade.HIGH;
        return entity;
    }

    private static void update(final AllTypesEntity entity) {
        entity.primitiveBoolean = false;
        entity.wrapperBoolean = Boolean.FALSE;
        entity.primitiveByte = (byte) 1;
        entity.wrapperByte = (byte) -1;
        entity.primitiveShort = (short) 1;
        entity.wrapperShort = (short) -1;
        entity.primitiveInt = 1;
        entity.wrapperInt = -1;
        entity.primitiveLong = 1L;
        entity.wrapperLong = -1L;
        entity.primitiveChar = 'z';
        entity.wrapperChar = 'z';
        entity.primitiveFloat = 1.5f;
        entity.wrapperFloat = -1.5f;
        entity.primitiveDouble = 1.125d;
        entity.wrapperDouble = -1.125d;
        entity.string = "John Doe";
        entity.uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        entity.bigInteger = BigInteger.ONE;
        entity.bigDecimal = new BigDecimal("-0.001");
        entity.localDate = LocalDate.of(2000, 2, 29);
        entity.localTime = LocalTime.MIDNIGHT;
        entity.localDateTime = LocalDateTime.of(2000, 2, 29, 23, 59, 59);
        entity.offsetTime = OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC);
        entity.offsetDateTime = OffsetDateTime.of(2000, 2, 29, 0, 0, 0, 0, ZoneOffset.UTC);
        entity.instant = Instant.EPOCH;
        entity.year = Year.of(-1);
        entity.utilDate = new java.util.Date(0L);
        entity.sqlDate = java.sql.Date.valueOf("2000-02-29");
        entity.sqlTime = java.sql.Time.valueOf("23:59:59");
        entity.sqlTimestamp = java.sql.Timestamp.valueOf("2000-02-29 00:00:00.000000001");
        entity.bytes = new byte[0];
        entity.wrapperBytes = new Byte[0];
        entity.chars = new char[0];
        entity.wrapperChars = new Character[0];
        entity.grade = AllTypesEntity.Grade.LOW;
    }

    private static void assertSameValues(final AllTypesEntity actual, final AllTypesEntity expected) {
        assertThat(actual.primitiveBoolean).as("primitiveBoolean").isEqualTo(expected.primitiveBoolean);
        assertThat(actual.wrapperBoolean).as("wrapperBoolean").isEqualTo(expected.wrapperBoolean);
        assertThat(actual.primitiveByte).as("primitiveByte").isEqualTo(expected.primitiveByte);
        assertThat(actual.wrapperByte).as("wrapperByte").isEqualTo(expected.wrapperByte);
        assertThat(actual.primitiveShort).as("primitiveShort").isEqualTo(expected.primitiveShort);
        assertThat(actual.wrapperShort).as("wrapperShort").isEqualTo(expected.wrapperShort);
        assertThat(actual.primitiveInt).as("primitiveInt").isEqualTo(expected.primitiveInt);
        assertThat(actual.wrapperInt).as("wrapperInt").isEqualTo(expected.wrapperInt);
        assertThat(actual.primitiveLong).as("primitiveLong").isEqualTo(expected.primitiveLong);
        assertThat(actual.wrapperLong).as("wrapperLong").isEqualTo(expected.wrapperLong);
        assertThat(actual.primitiveChar).as("primitiveChar").isEqualTo(expected.primitiveChar);
        assertThat(actual.wrapperChar).as("wrapperChar").isEqualTo(expected.wrapperChar);
        assertThat(actual.primitiveFloat).as("primitiveFloat").isEqualTo(expected.primitiveFloat);
        assertThat(actual.wrapperFloat).as("wrapperFloat").isEqualTo(expected.wrapperFloat);
        assertThat(actual.primitiveDouble).as("primitiveDouble").isEqualTo(expected.primitiveDouble);
        assertThat(actual.wrapperDouble).as("wrapperDouble").isEqualTo(expected.wrapperDouble);
        assertThat(actual.string).as("string").isEqualTo(expected.string);
        assertThat(actual.uuid).as("uuid").isEqualTo(expected.uuid);
        assertThat(actual.bigInteger).as("bigInteger").isEqualTo(expected.bigInteger);
        assertThat(actual.bigDecimal).as("bigDecimal").isEqualTo(expected.bigDecimal);
        assertThat(actual.localDate).as("localDate").isEqualTo(expected.localDate);
        assertThat(actual.localTime).as("localTime").isEqualTo(expected.localTime);
        assertThat(actual.localDateTime).as("localDateTime").isEqualTo(expected.localDateTime);
        assertThat(actual.offsetTime).as("offsetTime").isEqualTo(expected.offsetTime);
        assertThat(actual.offsetDateTime).as("offsetDateTime").isEqualTo(expected.offsetDateTime);
        assertThat(actual.instant).as("instant").isEqualTo(expected.instant);
        assertThat(actual.year).as("year").isEqualTo(expected.year);
        assertThat(actual.utilDate).as("utilDate").isEqualTo(expected.utilDate);
        assertThat(actual.sqlDate).as("sqlDate").isEqualTo(expected.sqlDate);
        assertThat(actual.sqlTime).as("sqlTime").isEqualTo(expected.sqlTime);
        assertThat(actual.sqlTimestamp).as("sqlTimestamp").isEqualTo(expected.sqlTimestamp);
        assertThat(actual.bytes).as("bytes").isEqualTo(expected.bytes);
        assertThat(actual.wrapperBytes).as("wrapperBytes").isEqualTo(expected.wrapperBytes);
        assertThat(actual.chars).as("chars").isEqualTo(expected.chars);
        assertThat(actual.wrapperChars).as("wrapperChars").isEqualTo(expected.wrapperChars);
        assertThat(actual.grade).as("grade").isEqualTo(expected.grade);
    }

    /**
     * Reads the nullable plaintext columns of the row behind the entity, bypassing the entity and its callbacks.
     */
    private static Object[] plaintextColumns(final long id) {
        return apply(em -> (Object[]) em
                .createNativeQuery("SELECT "
                                   + "wrapper_boolean, wrapper_byte, wrapper_short, wrapper_int, wrapper_long, "
                                   + "wrapper_char, wrapper_float, wrapper_double, string_, uuid_, big_integer, "
                                   + "big_decimal, local_date, local_time, local_date_time, offset_time, "
                                   + "offset_date_time, instant_, year_, util_date, sql_date, sql_time, "
                                   + "sql_timestamp, bytes_, wrapper_bytes, chars_, wrapper_chars, grade"
                                   + " FROM " + AllTypesEntity.TABLE_NAME + " WHERE id = " + id)
                .getSingleResult());
    }

    @DisplayName("every supported type survives persist, reload, update and reload; no plaintext reaches the row")
    @Test
    void __roundTrip() {
        final var expected = populated();
        final long id = apply(em -> {
            final var entity = populated();
            em.persist(entity);
            return entity;
        }).id;
        assertThat(plaintextColumns(id)).as("after the INSERT").containsOnlyNulls();

        final var loaded = apply(em -> em.find(AllTypesEntity.class, id));
        assertSameValues(loaded, expected);

        update(expected);
        apply(em -> {
            final var entity = em.find(AllTypesEntity.class, id);
            update(entity);
            return entity;
        });
        assertThat(plaintextColumns(id)).as("after the UPDATE").containsOnlyNulls();

        final var reloaded = apply(em -> em.find(AllTypesEntity.class, id));
        assertSameValues(reloaded, expected);
    }
}
