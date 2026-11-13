package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.eclipselink.examples.AllTypesEntity;
import com.github.jinahya.persistence.encryption.eclipselink.examples.Color;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * One named converter on every supported type, an enum and a primitive: all round-trip, every column is
 * binary. An in-place change of a mutable value is covered by EncryptedConverter_Test: not detected on EclipseLink.
 */
@SuppressWarnings({"java:S101"})
class AllTypes_Test {

    private static EntityManagerFactory EMF;

    @BeforeAll
    static void open() {
        EMF = Persistence.createEntityManagerFactory("__encryptionEclipseLinkPU");
    }

    @AfterAll
    static void close() {
        if (EMF != null) {
            EMF.close();
        }
    }

    static AllTypesEntity filled() {
        final var e = new AllTypesEntity();
        e.aBoolean = true;
        e.aByte = (byte) -7;
        e.aShort = (short) -300;
        e.aCharacter = 'é';
        e.anInteger = -70000;
        e.aFloat = 1.5f;
        e.aLong = Long.MIN_VALUE + 1;
        e.aDouble = Math.PI;
        e.aString = "é😀 string";
        e.aUuid = UUID.randomUUID();
        e.aBigInteger = new BigInteger("-123456789012345678901234567890");
        e.aBigDecimal = new BigDecimal("-1234567.8901234567");
        e.aLocalDate = LocalDate.of(1999, 12, 31);
        e.aLocalTime = LocalTime.of(23, 59, 58, 123456789);
        e.aLocalDateTime = LocalDateTime.of(2000, 2, 29, 1, 2, 3, 4);
        e.anOffsetTime = OffsetTime.of(LocalTime.of(1, 2, 3, 4), ZoneOffset.ofHours(9));
        e.anOffsetDateTime = OffsetDateTime.of(LocalDateTime.of(2001, 1, 1, 0, 0), ZoneOffset.ofHoursMinutes(-3, -30));
        e.anInstant = Instant.ofEpochSecond(1234567890L, 987654321L);
        e.aYear = Year.of(2038);
        e.aUtilDate = new java.util.Date(1234567890123L);
        e.aSqlDate = new java.sql.Date(1234567890123L);
        e.aSqlTime = new java.sql.Time(1234567890123L);
        e.aSqlTimestamp = java.sql.Timestamp.from(Instant.ofEpochSecond(1234567890L, 987654321L));
        e.bytes = new byte[] {1, 2, 3, -4};
        e.bytesWrapped = new Byte[] {5, 6, 7};
        e.chars = new char[] {'a', 'é', 'c'};
        e.charsWrapped = new Character[] {'x', 'y'};
        e.aColor = Color.GREEN;
        e.anInt = 42;
        return e;
    }

    static List<Field> encryptedFields() {
        final var fields = new ArrayList<Field>();
        for (final var field : AllTypesEntity.class.getFields()) {
            if (!field.getName().equals("id") && !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        return fields;
    }

    @DisplayName("every supported type reads back equal")
    @Test
    void roundTrip() throws ReflectiveOperationException {
        final var original = filled();
        final var id = PersistenceTests.apply(EMF, em -> {
            em.persist(original);
            return original;
        }).id;
        final var loaded = PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id));
        for (final var field : encryptedFields()) {
            assertThat(field.get(loaded)).as(field.getName()).isEqualTo(field.get(original));
        }
    }

    @DisplayName("every encrypted column is binary, whatever the java type")
    @Test
    void columnsAreBinary() {
        for (final var field : encryptedFields()) {
            final var column = field.getAnnotation(jakarta.persistence.Column.class).name();
            assertThat(PersistenceTests.columnType(EMF, AllTypesEntity.TABLE_NAME, column)).as(column)
                    .contains("BINARY");
        }
    }



    private static long rows() {
        return ((Number) PersistenceTests.apply(EMF, em -> em.createNativeQuery(
                "SELECT COUNT(*) FROM " + AllTypesEntity.TABLE_NAME).getSingleResult())).longValue();
    }

    @DisplayName("a value longer than the declared length is stored whole: the platform type has no length to exceed")
    @Test
    void tooLong() {
        final var rows = rows();
        final var value = "x".repeat(2048 + 1);
        final var id = PersistenceTests.apply(EMF, em -> {
            final var e = new AllTypesEntity();
            e.aString = value;
            em.persist(e);
            return e;
        }).id;
        assertThat(rows()).isEqualTo(rows + 1);
        assertThat(PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id)).aString).isEqualTo(value);
    }

    @DisplayName("the column lengths: the platform's type for byte[], LONGVARBINARY, whose length is the platform's,"
                 + " not the declared 2048")
    @Test
    void columnLengths() {
        for (final var field : encryptedFields()) {
            final var column = field.getAnnotation(jakarta.persistence.Column.class).name();
            assertThat(PersistenceTests.columnLength(EMF, AllTypesEntity.TABLE_NAME, column)).as(column)
                    .isGreaterThan(2048L);
        }
    }

    @DisplayName("a null of every type is a NULL column, and reads back null; a primitive reads its default")
    @Test
    void nulls() throws ReflectiveOperationException {
        final var id = PersistenceTests.apply(EMF, em -> {
            final var e = new AllTypesEntity();
            em.persist(e);
            return e;
        }).id;
        final var loaded = PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id));
        for (final var field : encryptedFields()) {
            final var column = field.getAnnotation(jakarta.persistence.Column.class).name();
            final var stored = PersistenceTests.apply(EMF, em -> em.createNativeQuery(
                    "SELECT " + column + " FROM " + AllTypesEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
            if (field.getType().isPrimitive()) {
                assertThat(stored).as(column).isNotNull();
                assertThat(field.get(loaded)).as(field.getName()).isEqualTo(0);
                continue;
            }
            assertThat(stored).as("%s is SQL NULL", column).isNull();
            assertThat(field.get(loaded)).as(field.getName()).isNull();
        }
    }
}
