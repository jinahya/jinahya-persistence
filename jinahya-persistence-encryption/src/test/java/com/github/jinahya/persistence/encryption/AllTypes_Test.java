package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.AllTypesEntity;
import com.github.jinahya.persistence.encryption.examples.Color;
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

/**
 * Round-trips one attribute of every supported type through its converter, and checks that every column is binary.
 */
@SuppressWarnings({"java:S101"})
class AllTypes_Test {

    private static EntityManagerFactory EMF;

    @BeforeAll
    static void open() {
        EMF = Persistence.createEntityManagerFactory("__encryptionPU");
    }

    @AfterAll
    static void close() {
        if (EMF != null) {
            EMF.close();
        }
    }

    private static AllTypesEntity filled() {
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

    private static List<Field> encryptedFields() {
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

    @DisplayName("an array changed in place: detected on Hibernate ORM, not on EclipseLink; assigned anew, stored")
    @Test
    void arrayInPlace() {
        final var id = PersistenceTests.apply(EMF, em -> {
            final var e = filled();
            em.persist(e);
            return e;
        }).id;
        PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id).bytes[0] = 99);
        final var loaded = PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id));
        if (PersistenceTests.isHibernate(EMF)) {
            // the converted type keeps the domain type's mutability plan: a copy is kept, and compared
            assertThat(loaded.bytes[0]).isEqualTo((byte) 99);
        } else {
            // a converted mapping is immutable unless @Mutable: the instance is compared with itself
            assertThat(loaded.bytes[0]).isEqualTo((byte) 1);
        }
        PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id).bytes = new byte[] {77, 2, 3, -4});
        assertThat(PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id)).bytes)
                .isEqualTo(new byte[] {77, 2, 3, -4});
    }

    @DisplayName("a Date changed in place: detected on Hibernate ORM, not on EclipseLink; assigned anew, stored")
    @Test
    void dateInPlace() {
        final var id = PersistenceTests.apply(EMF, em -> {
            final var e = filled();
            em.persist(e);
            return e;
        }).id;
        PersistenceTests.apply(EMF, em -> {
            em.find(AllTypesEntity.class, id).aUtilDate.setTime(0L);
            return null;
        });
        final var loaded = PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id));
        if (PersistenceTests.isHibernate(EMF)) {
            assertThat(loaded.aUtilDate.getTime()).isZero();
        } else {
            assertThat(loaded.aUtilDate.getTime()).isEqualTo(1234567890123L);
        }
        PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id).aUtilDate = new java.util.Date(5_000L));
        assertThat(PersistenceTests.apply(EMF, em -> em.find(AllTypesEntity.class, id)).aUtilDate.getTime())
                .isEqualTo(5_000L);
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
