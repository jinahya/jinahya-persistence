package com.github.jinahya.persistence.more.temporalinterval;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the interval mapped superclasses map as declared against a real persistence provider.
 * <p>
 * What the unit tests cannot reach is here: the names of the columns, the types the provider chooses for them, whether
 * a value survives a write and a read, whether an inverted interval is accepted at persist time, and whether the
 * containment this package is written for actually runs in SQL.
 * <p>
 * The classes differ only in their point type, so {@link Case} states the expectations once and each nested class
 * supplies a point type and two samples, as in {@link ___MappedTemporalInterval_Test}. What is particular to one class
 * is tested beside its own case.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class ___MappedTemporalInterval_PersistenceTest {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__intervalPU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
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

    /**
     * Runs specified action, and returns the {@link ConstraintViolationException} it failed with.
     * <p>
     * The exception is searched for down the cause chain, and the violations are read from the exception itself rather
     * than from its message: Hibernate throws it unwrapped where EclipseLink wraps it.
     */
    private static ConstraintViolationException constraintViolationOf(final Runnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            for (Throwable c = e; c != null; c = c.getCause()) {
                if (c instanceof ConstraintViolationException cve) {
                    return cve;
                }
            }
            throw new AssertionError("not a constraint violation", e);
        }
        throw new AssertionError("no constraint violation was raised");
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

    private static String columnTypeOf(final String tableName, final String columnName) {
        final List<?> rows = applyEntityManager(em -> em
                .createNativeQuery("select upper(DATA_TYPE) from INFORMATION_SCHEMA.COLUMNS"
                                   + " where lower(TABLE_NAME) = lower(?1) and lower(COLUMN_NAME) = lower(?2)")
                .setParameter(1, tableName)
                .setParameter(2, columnName)
                .getResultList());
        assertThat(rows).as("column %s of %s", columnName, tableName).hasSize(1);
        return String.valueOf(rows.get(0));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * What every mapped superclass in this package is expected to do against a database, written once.
     *
     * @param <E> the entity type under test
     * @param <P> its point type
     */
    abstract static class Case<E extends ___MappedTemporalInterval<P>, P extends Temporal & Comparable<? super P>> {

        private final Class<E> entityClass;

        private final Supplier<E> instantiator;

        private final String tableName;

        private final P earlier;

        private final P later;

        Case(final Class<E> entityClass, final Supplier<E> instantiator, final String tableName, final P earlier,
             final P later) {
            this.entityClass = entityClass;
            this.instantiator = instantiator;
            this.tableName = tableName;
            this.earlier = earlier;
            this.later = later;
        }

        /**
         * Asserts that a point read back is the point which was written.
         *
         * @param actual   the point read back.
         * @param expected the point which was written.
         * @implSpec The default implementation asserts equality. A subclass whose point type carries more than
         *         the database keeps overrides this — see {@link OfOffsetDateTime}.
         */
        void assertSamePoint(final P actual, final P expected) {
            assertThat(actual).isEqualTo(expected);
        }

        private E entity(final P start, final P end) {
            final E instance = instantiator.get();
            instance.setIntervalStart(start);
            instance.setIntervalEnd(end);
            return instance;
        }

        // ------------------------------------------------------------------------------------------------ the columns

        @DisplayName("the two points are mapped to interval_start and interval_end, and nothing else is")
        @Test
        void _twoColumns_declaredNames() {
            assertThat(columnNamesOf(tableName))
                    .contains(___MappedTemporalInterval.COLUMN_NAME_INTERVAL_START,
                              ___MappedTemporalInterval.COLUMN_NAME_INTERVAL_END)
                    .as("no @Transient accessor leaks into a column of its own")
                    .doesNotContain("startnotafterend", "start_not_after_end", "temporalamount", "temporal_amount");
        }

        @DisplayName("neither column is stored as an opaque blob")
        @Test
        void _notSerialized_columnType() {
            for (final var column : List.of(___MappedTemporalInterval.COLUMN_NAME_INTERVAL_START,
                                            ___MappedTemporalInterval.COLUMN_NAME_INTERVAL_END)) {
                assertThat(columnTypeOf(tableName, column))
                        .as("a serialized point cannot answer a containment")
                        .doesNotContain("BINARY")
                        .doesNotContain("BLOB");
            }
        }

        // --------------------------------------------------------------------------------------------- the round trip

        @DisplayName("a bounded interval survives a write and a read")
        @Test
        void _survives_bounded() {
            persistAndFind(entity(earlier, later), entityClass, found -> {
                assertSamePoint(found.getIntervalStart(), earlier);
                assertSamePoint(found.getIntervalEnd(), later);
                return null;
            });
        }

        @DisplayName("an absent bound is read back absent, not as some zero of the type")
        @Test
        void _survives_absentBound() {
            persistAndFind(entity(earlier, null), entityClass, found -> {
                assertSamePoint(found.getIntervalStart(), earlier);
                assertThat(found.getIntervalEnd()).isNull();
                return null;
            });
            persistAndFind(entity(null, later), entityClass, found -> {
                assertThat(found.getIntervalStart()).isNull();
                assertSamePoint(found.getIntervalEnd(), later);
                return null;
            });
        }

        // ------------------------------------------------------------------------------------------ the absent order

        @DisplayName("an inverted interval persists: the order of the two points is not validated here")
        @Test
        void _accepted_onPersist() {
            final var found = persistAndFind(entity(later, earlier), entityClass, E::getIntervalStart);
            assertSamePoint(found, later);
        }

        @DisplayName("and it is inert under containment, matching no point at all")
        @Test
        void _inert_underContainment() {
            final var inverted = entity(later, earlier);
            assertThat(inverted.contains(earlier)).isFalse();
            assertThat(inverted.contains(later)).isFalse();
        }

        // --------------------------------------------------------------------------------------------- the containment

        @DisplayName("the containment this package is stored for runs in SQL, absent bounds included")
        @Test
        void _containment_inSql() {
            assertSamePoint(persistAndFind(entity(earlier, later), entityClass, E::getIntervalStart), earlier);
            final Long matches = applyEntityManager(em -> em
                    .createQuery("select count(e) from " + entityClass.getSimpleName() + " e"
                                 + " where (e.intervalStart is null or e.intervalStart <= :point)"
                                 + "   and (e.intervalEnd is null or e.intervalEnd > :point)", Long.class)
                    .setParameter("point", earlier)
                    .getSingleResult());
            assertThat(matches)
                    .as("the start is inclusive, so the interval starting at the point contains it")
                    .isPositive();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nested
    class OfLocalDate extends Case<_LocalDateIntervalEntity, LocalDate> {

        OfLocalDate() {
            super(_LocalDateIntervalEntity.class, _LocalDateIntervalEntity::new,
                  _LocalDateIntervalEntity.TABLE_NAME, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 5));
        }
    }

    @Nested
    class OfLocalTime extends Case<_LocalTimeIntervalEntity, LocalTime> {

        OfLocalTime() {
            super(_LocalTimeIntervalEntity.class, _LocalTimeIntervalEntity::new,
                  _LocalTimeIntervalEntity.TABLE_NAME, LocalTime.of(9, 0), LocalTime.of(17, 0));
        }
    }

    @Nested
    class OfLocalDateTime extends Case<_LocalDateTimeIntervalEntity, LocalDateTime> {

        OfLocalDateTime() {
            super(_LocalDateTimeIntervalEntity.class, _LocalDateTimeIntervalEntity::new,
                  _LocalDateTimeIntervalEntity.TABLE_NAME, LocalDateTime.of(2026, 1, 1, 9, 0),
                  LocalDateTime.of(2026, 1, 1, 17, 0));
        }
    }

    @Nested
    class OfInstant extends Case<_InstantIntervalEntity, Instant> {

        OfInstant() {
            super(_InstantIntervalEntity.class, _InstantIntervalEntity::new,
                  _InstantIntervalEntity.TABLE_NAME, Instant.parse("2026-01-01T09:00:00Z"),
                  Instant.parse("2026-01-01T17:00:00Z"));
        }
    }

    @Nested
    class OfOffsetDateTime extends Case<_OffsetDateTimeIntervalEntity, OffsetDateTime> {

        OfOffsetDateTime() {
            super(_OffsetDateTimeIntervalEntity.class, _OffsetDateTimeIntervalEntity::new,
                  _OffsetDateTimeIntervalEntity.TABLE_NAME,
                  OffsetDateTime.of(2026, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC),
                  OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        }

        @Override
        void assertSamePoint(final OffsetDateTime actual, final OffsetDateTime expected) {
            // the moment is what this column keeps. the offset is not: H2 and SQL Server store it, PostgreSQL renders
            // a stored UTC value in the session's zone, and a provider may hand back the JVM's — EclipseLink does.
            assertThat(actual.toInstant()).isEqualTo(expected.toInstant());
        }

        @DisplayName("the moment survives a write and a read, whatever offset it comes back wearing")
        @Test
        void _momentSurvives_offsetMayNot() {
            final var written = OffsetDateTime.of(2026, 3, 1, 9, 0, 0, 0, ZoneOffset.ofHours(2));
            final var entity = new _OffsetDateTimeIntervalEntity();
            entity.setIntervalStart(written);
            persistAndFind(entity, _OffsetDateTimeIntervalEntity.class, found -> {
                assertThat(found.getIntervalStart()).isNotNull();
                assertThat(found.getIntervalStart().toInstant()).isEqualTo(written.toInstant());
                return null;
            });
        }

        @DisplayName("an interval whose ends name one moment in two offsets persists, being empty and not inverted")
        @Test
        void _persisted_emptyAcrossOffsets() {
            final var entity = new _OffsetDateTimeIntervalEntity();
            entity.setIntervalStart(OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.ofHours(2)));
            entity.setIntervalEnd(OffsetDateTime.of(2026, 9, 20, 8, 0, 0, 0, ZoneOffset.UTC));
            persistAndFind(entity, _OffsetDateTimeIntervalEntity.class, found -> {
                assertThat(found.getIntervalStart()).isNotNull();
                assertThat(found.getIntervalEnd()).isNotNull();
                assertThat(found.getIntervalStart().toInstant())
                        .as("the moment survives, whatever the database does with the offset")
                        .isEqualTo(found.getIntervalEnd().toInstant());
                return null;
            });
        }
    }

    @Nested
    class OfYear extends Case<_YearIntervalEntity, Year> {

        OfYear() {
            super(_YearIntervalEntity.class, _YearIntervalEntity::new,
                  _YearIntervalEntity.TABLE_NAME, Year.of(2026), Year.of(2029));
        }

        @DisplayName("a year is kept as a number, which is the only form of it that sorts")
        @Test
        void _numeric_column() {
            assertThat(columnTypeOf(_YearIntervalEntity.TABLE_NAME,
                                    ___MappedTemporalInterval.COLUMN_NAME_INTERVAL_START))
                    .as("Year.toString writes 999 and 2026 at different widths, so text would sort wrongly")
                    .containsAnyOf("INT", "NUMERIC", "DECIMAL");
        }
    }

    @Nested
    class OfYearMonth extends Case<_YearMonthIntervalEntity, YearMonth> {

        OfYearMonth() {
            super(_YearMonthIntervalEntity.class, _YearMonthIntervalEntity::new,
                  _YearMonthIntervalEntity.TABLE_NAME, YearMonth.of(2026, 1), YearMonth.of(2027, 3));
        }

        @DisplayName("the converter is applied, so the column is text and not a serialized object")
        @Test
        void _converted_column() {
            assertThat(columnTypeOf(_YearMonthIntervalEntity.TABLE_NAME,
                                    ___MappedTemporalInterval.COLUMN_NAME_INTERVAL_START))
                    .as("unconverted, a YearMonth falls through to the serializable rule")
                    .containsAnyOf("CHAR", "VARCHAR");
        }

        @DisplayName("the ISO form is fixed-width, so the column sorts chronologically even across widths")
        @Test
        void _sortsChronologically_paddedYear() {
            final var earlier = new _YearMonthIntervalEntity();
            earlier.setIntervalStart(YearMonth.of(999, 11));
            earlier.setIntervalEnd(YearMonth.of(1000, 2));
            final var later = new _YearMonthIntervalEntity();
            later.setIntervalStart(YearMonth.of(2026, 1));
            later.setIntervalEnd(YearMonth.of(2026, 2));
            applyEntityManager(em -> {
                em.persist(earlier);
                em.persist(later);
                em.flush();
                return null;
            });
            // distinct: the persistence unit is one database, and the cases above have written 2026-01 too
            final List<YearMonth> ordered = applyEntityManager(em -> em
                    .createQuery("select distinct e.intervalStart from _YearMonthIntervalEntity e"
                                 + " where e.intervalStart in :points order by e.intervalStart", YearMonth.class)
                    .setParameter("points", List.of(YearMonth.of(999, 11), YearMonth.of(2026, 1)))
                    .getResultList());
            assertThat(ordered)
                    .as("0999-11 is seven characters too, so it sorts before 2026-01")
                    .containsExactly(YearMonth.of(999, 11), YearMonth.of(2026, 1));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @DisplayName("a table may carry two intervals, through an embeddable, in columns of its own choosing")
    @Nested
    class TwoIntervalsTest {

        @DisplayName("neither pair of default column names is written to the table")
        @Test
        void _overriddenColumns_both() {
            assertThat(columnNamesOf(_BookingEntity.TABLE_NAME))
                    .contains(_BookingEntity.COLUMN_NAME_STAY_START, _BookingEntity.COLUMN_NAME_STAY_END,
                              _BookingEntity.COLUMN_NAME_HOLD_START, _BookingEntity.COLUMN_NAME_HOLD_END)
                    .doesNotContain(___MappedTemporalInterval.COLUMN_NAME_INTERVAL_START,
                                    ___MappedTemporalInterval.COLUMN_NAME_INTERVAL_END);
        }

        @DisplayName("both intervals survive a write and a read, independently")
        @Test
        void _survives_both() {
            final var booking = new _BookingEntity();
            final var stay = new _DateIntervalEmbeddable();
            stay.setIntervalStart(LocalDate.of(2026, 5, 1));
            stay.setIntervalEnd(LocalDate.of(2026, 5, 8));
            final var hold = new _DateIntervalEmbeddable();
            hold.setIntervalStart(LocalDate.of(2026, 4, 1));
            booking.setStay(stay);
            booking.setHold(hold);
            persistAndFind(booking, _BookingEntity.class, found -> {
                assertThat(found.getStay()).isNotNull();
                assertThat(found.getStay().getIntervalStart()).isEqualTo(LocalDate.of(2026, 5, 1));
                assertThat(found.getStay().getIntervalEnd()).isEqualTo(LocalDate.of(2026, 5, 8));
                assertThat(found.getStay().getTemporalAmount()).isNotNull();
                assertThat(found.getHold()).isNotNull();
                assertThat(found.getHold().getIntervalStart()).isEqualTo(LocalDate.of(2026, 4, 1));
                assertThat(found.getHold().getIntervalEnd())
                        .as("an open-ended hold stays open")
                        .isNull();
                return null;
            });
        }
    }
}
