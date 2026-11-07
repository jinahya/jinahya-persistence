package com.github.jinahya.persistence.more.utcdatetime;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the mapped superclasses in this package map as declared against a real persistence provider.
 * <p>
 * The JVM's default zone is set, for the whole class, to one with daylight saving, so that every value below is read
 * back through a zone with transitions in it. A UTC date-time which falls in that zone's spring-forward gap is the one
 * value which does not survive; see {@link Gap}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class _MappedUtcDateTime_PersistenceTest {

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        // before the factory is built, so that nothing it caches has seen another zone
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__utcDateTimePU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        try {
            if (ENTITY_MANAGER_FACTORY != null) {
                ENTITY_MANAGER_FACTORY.close();
            }
        } finally {
            TimeZone.setDefault(DEFAULT_TIME_ZONE);
        }
    }

    private static <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        try (final var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                final var result = function.apply(entityManager);
                transaction.commit();
                return result;
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    private static <T, R> R persistAndFind(final T entity, final Class<T> entityClass,
                                           final Function<? super T, ? extends R> function) {
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        return applyEntityManager(em -> function.apply(em.find(entityClass, id)));
    }

    private static List<String> columnNamesOf(final String tableName) {
        // untyped: EclipseLink reads a result class as an entity class, and has no descriptor for String
        final List<?> rows = applyEntityManager(em -> em
                .createNativeQuery("select lower(COLUMN_NAME) from INFORMATION_SCHEMA.COLUMNS"
                                   + " where lower(TABLE_NAME) = lower(?1)")
                .setParameter(1, tableName)
                .getResultList());
        return rows.stream().map(String::valueOf).toList();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Zoned date-times which a database has reason to get wrong, each written in microseconds, which every column
     * here keeps.
     */
    private static final List<ZonedDateTime> ZONED_DATE_TIMES = List.of(
            ZonedDateTime.of(2026, 9, 20, 10, 0, 0, 123_456_000, ZoneId.of("Asia/Seoul")),
            // past the end of MySQL's TIMESTAMP, 2038-01-19T03:14:07Z
            ZonedDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")),
            // before the epoch, at Seoul's local mean time of +08:27:52
            ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul")),
            // the later of the two offsets of an overlap
            ZonedDateTime.of(2026, 11, 1, 1, 30, 0, 0, ZoneId.of("America/New_York")).withLaterOffsetAtOverlap(),
            // an offset as the zone
            ZonedDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.ofHours(9))
    );

    @Nested
    class OfZonedDateTime {

        @DisplayName("the two attributes are mapped to utc_date_time and zone_id, and nothing else is")
        @Test
        void _twoColumns_declaredNames() {
            assertThat(columnNamesOf(_ZonedDateTimeEntity.TABLE_NAME))
                    .containsExactlyInAnyOrder("id", _MappedUtcDateTime.COLUMN_NAME_UTC_DATE_TIME,
                                               MappedZonedDateTime.COLUMN_NAME_ZONE_ID);
        }

        @DisplayName("a zoned date-time survives a write and a read, zone and offset and all")
        @Test
        void _roundTrip() {
            for (final var written : ZONED_DATE_TIMES) {
                final var entity = new _ZonedDateTimeEntity();
                entity.setZonedDateTime(written);
                final var found = persistAndFind(entity, _ZonedDateTimeEntity.class,
                                                 _ZonedDateTimeEntity::getZonedDateTime);
                assertThat(found).as("read back of %s", written).isEqualTo(written);
            }
        }
    }

    @Nested
    class OfOffsetDateTime {

        @DisplayName("the two attributes are mapped to utc_date_time and zone_offset, and nothing else is")
        @Test
        void _twoColumns_declaredNames() {
            assertThat(columnNamesOf(_OffsetDateTimeEntity.TABLE_NAME))
                    .containsExactlyInAnyOrder("id", _MappedUtcDateTime.COLUMN_NAME_UTC_DATE_TIME,
                                               MappedOffsetDateTime.COLUMN_NAME_ZONE_OFFSET);
        }

        @DisplayName("an offset date-time survives a write and a read, offset and all")
        @Test
        void _roundTrip() {
            for (final var zoned : ZONED_DATE_TIMES) {
                final var written = zoned.toOffsetDateTime();
                final var entity = new _OffsetDateTimeEntity();
                entity.setOffsetDateTime(written);
                final var found = persistAndFind(entity, _OffsetDateTimeEntity.class,
                                                 _OffsetDateTimeEntity::getOffsetDateTime);
                assertThat(found).as("read back of %s", written).isEqualTo(written);
            }
        }
    }

    /**
     * A UTC date-time in a spring-forward gap of the JVM's default zone does not survive, on either provider.
     * <p>
     * Both providers carry a {@link LocalDateTime} through a {@link java.sql.Timestamp} in the default zone, where
     * {@code 2026-03-08T02:30} does not exist and becomes {@code 03:30}. H2 itself keeps the value, through
     * {@link java.sql.PreparedStatement#setObject(int, Object) setObject}; the shift is the providers'. These tests
     * record it, so that a provider which stops doing it is noticed; see {@link _MappedUtcDateTime}.
     */
    @Nested
    class Gap {

        private static final ZonedDateTime WRITTEN =
                ZonedDateTime.of(2026, 3, 8, 2, 30, 0, 0, ZoneOffset.UTC).withZoneSameInstant(ZoneId.of("Asia/Tokyo"));

        @DisplayName("a zoned date-time in the gap of the default zone is read back the length of the gap later")
        @Test
        void _zoned_shifted() {
            final var entity = new _ZonedDateTimeEntity();
            entity.setZonedDateTime(WRITTEN);
            final var found = persistAndFind(entity, _ZonedDateTimeEntity.class,
                                             _ZonedDateTimeEntity::getZonedDateTime);
            assertThat(found).isEqualTo(WRITTEN.plusHours(1));
        }

        @DisplayName("an offset date-time in the gap of the default zone is read back the length of the gap later")
        @Test
        void _offset_shifted() {
            final var written = WRITTEN.toOffsetDateTime();
            final var entity = new _OffsetDateTimeEntity();
            entity.setOffsetDateTime(written);
            final var found = persistAndFind(entity, _OffsetDateTimeEntity.class,
                                             _OffsetDateTimeEntity::getOffsetDateTime);
            assertThat(found).isEqualTo(written.plusHours(1));
        }
    }

    @Nested
    class Embedded {

        @DisplayName("each embedding takes the column names it overrides")
        @Test
        void _overriddenNames() {
            assertThat(columnNamesOf(_EventEntity.TABLE_NAME))
                    .containsExactlyInAnyOrder("id",
                                               _EventEntity.COLUMN_NAME_OCCURRED_UTC,
                                               _EventEntity.COLUMN_NAME_OCCURRED_ZONE,
                                               _EventEntity.COLUMN_NAME_RECORDED_UTC,
                                               _EventEntity.COLUMN_NAME_RECORDED_ZONE,
                                               _EventEntity.COLUMN_NAME_SIGNED_UTC,
                                               _EventEntity.COLUMN_NAME_SIGNED_OFFSET);
        }

        @DisplayName("the moments compare in SQL past 2038, whatever zone each was written in")
        @Test
        void _comparedInSql() {
            final var occurred = new _ZonedDateTimeEmbeddable();
            occurred.setZonedDateTime(ZonedDateTime.of(2100, 1, 1, 8, 0, 0, 0, ZoneId.of("Asia/Seoul")));
            final var signed = new _OffsetDateTimeEmbeddable();
            signed.setOffsetDateTime(OffsetDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(-5)));
            final var entity = new _EventEntity();
            entity.setOccurred(occurred);
            entity.setSigned(signed);
            final var id = applyEntityManager(em -> {
                em.persist(entity);
                em.flush();
                return entity.getId();
            });
            // 2100-01-01T00:00Z, after the occurrence and before the signature
            final var point = LocalDateTime.of(2100, 1, 1, 0, 0);
            final List<Long> ids = applyEntityManager(em -> em
                    .createQuery("select e.id from _EventEntity e"
                                 + " where e.occurred.utcDateTime < :point and e.signed.utcDateTime > :point",
                                 Long.class)
                    .setParameter("point", point)
                    .getResultList());
            assertThat(ids).contains(id);
        }

        @DisplayName("an embedding left absent reads back as no value")
        @Test
        void _absent() {
            final var occurred = new _ZonedDateTimeEmbeddable();
            occurred.setZonedDateTime(ZonedDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneId.of("Asia/Seoul")));
            final var entity = new _EventEntity();
            entity.setOccurred(occurred);
            persistAndFind(entity, _EventEntity.class, found -> {
                // Hibernate reads an embedding of all-null columns as null; EclipseLink as an instance of nulls
                assertThat(found.getRecorded() == null || found.getRecorded().getZonedDateTime() == null).isTrue();
                assertThat(found.getSigned() == null || found.getSigned().getOffsetDateTime() == null).isTrue();
                return null;
            });
        }
    }
}

