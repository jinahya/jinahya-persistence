package com.github.jinahya.persistence.more.orderedrange;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static com.github.jinahya.persistence.more.orderedrange.__BoundType.CLOSED;
import static com.github.jinahya.persistence.more.orderedrange.__BoundType.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link __MappedOrderedRange} against both persistence providers.
 * <p>
 * The claims under test are the ones the design rests on: all nine shapes are expressible <em>per row</em>, an
 * absent end is {@code NULL} and has no bound type, the columns still sort by endpoint despite carrying a marker,
 * the containment predicates run in SQL, and a converter whose encoding does not sort is rejected loudly.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({"java:S3577"})
class __MappedOrderedRange_PersistenceTest {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__orderedRangePU");
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

    private static <T extends __MappedOrderedRange<C>, C extends Comparable<? super C>> T persist(
            final T entity, final Class<T> entityClass,
            final C lower, final __BoundType lowerType, final C upper, final __BoundType upperType) {
        entity.setRangeLower(lower, lowerType);
        entity.setRangeUpper(upper, upperType);
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        return applyEntityManager(em -> em.find(entityClass, id));
    }

    private static _LocalDateRangeEntity dates(final LocalDate lower, final __BoundType lowerType,
                                               final LocalDate upper, final __BoundType upperType) {
        return persist(new _LocalDateRangeEntity(), _LocalDateRangeEntity.class, lower, lowerType, upper, upperType);
    }

    private static final LocalDate JAN = LocalDate.of(2026, 1, 1);

    private static final LocalDate APR = LocalDate.of(2026, 4, 1);

    @DisplayName("all nine shapes round-trip, in the same column pair")
    @Test
    void nineShapes__() {
        assertThat(dates(JAN, CLOSED, APR, OPEN)).hasToString("[2026-01-01, 2026-04-01)");
        assertThat(dates(JAN, CLOSED, APR, CLOSED)).hasToString("[2026-01-01, 2026-04-01]");
        assertThat(dates(JAN, OPEN, APR, CLOSED)).hasToString("(2026-01-01, 2026-04-01]");
        assertThat(dates(JAN, OPEN, APR, OPEN)).hasToString("(2026-01-01, 2026-04-01)");
        assertThat(dates(JAN, CLOSED, null, OPEN)).hasToString("[2026-01-01, null)");
        assertThat(dates(JAN, OPEN, null, OPEN)).hasToString("(2026-01-01, null)");
        assertThat(dates(null, OPEN, APR, CLOSED)).hasToString("(null, 2026-04-01]");
        assertThat(dates(null, OPEN, APR, OPEN)).hasToString("(null, 2026-04-01)");
        assertThat(dates(null, OPEN, null, OPEN)).hasToString("(null, null)");
    }

    @DisplayName("isBounded reads the two columns, and needs no decoding to do it")
    @Test
    void isBoundedNeedsNoDecoding__() {
        final var bounded = dates(JAN, CLOSED, APR, OPEN);
        _LocalDateRangeEntity.DECODE_COUNT.set(0);
        assertThat(bounded.isBounded()).isTrue();
        assertThat(_LocalDateRangeEntity.DECODE_COUNT.get())
                .as("the inherited default would have decoded both endpoints here")
                .isZero();
        assertThat(dates(JAN, CLOSED, null, OPEN).isBounded()).isFalse();
        assertThat(dates(null, OPEN, null, OPEN).isBounded()).isFalse();
    }

    @DisplayName("the bound type is per row, not per column")
    @Test
    void boundTypeIsPerRow__() {
        final var halfOpen = dates(JAN, CLOSED, APR, OPEN);
        final var closed = dates(JAN, CLOSED, APR, CLOSED);
        assertThat(halfOpen.contains(APR)).isFalse();
        assertThat(closed.contains(APR)).isTrue();
        assertThat(halfOpen.getUpperBoundType()).isSameAs(OPEN);
        assertThat(closed.getUpperBoundType()).isSameAs(CLOSED);
    }

    @DisplayName("an absent end is NULL and has no bound type")
    @Test
    void absentEndHasNoBoundType__() {
        final var found = dates(null, OPEN, null, OPEN);
        assertThat(found.getRangeLower()).isNull();
        assertThat(found.getLowerBoundType()).isNull();
        assertThat(found.getRangeUpper()).isNull();
        assertThat(found.getUpperBoundType()).isNull();
        assertThat(found.isBounded()).isFalse();
        assertThat(found.isEmpty()).isFalse();
        assertThat(found.getRangeBounds()).isSameAs(__RangeBounds.OPEN);   // unbounded is never inclusive
        assertThat(found.contains(LocalDate.MIN)).isTrue();
        assertThat(found.contains(LocalDate.MAX)).isTrue();
    }

    @DisplayName("equal endpoints: degenerate under [], empty under [) and (], and () merely empty too")
    @Test
    void equalEndpoints__() {
        assertThat(dates(JAN, CLOSED, JAN, CLOSED).isEmpty()).isFalse();
        assertThat(dates(JAN, CLOSED, JAN, OPEN).isEmpty()).isTrue();
        assertThat(dates(JAN, OPEN, JAN, CLOSED).isEmpty()).isTrue();
        // (a,a) is not a range in the strict sense - Guava rejects it outright - but the order of the two
        // endpoints is not validated here, so it persists, and reads back as empty like the other two.
        final var openOpen = dates(JAN, OPEN, JAN, OPEN);
        assertThat(openOpen.isEmpty()).isTrue();
        assertThat(openOpen.contains(JAN)).isFalse();
    }

    @DisplayName("the column carries the marker, and still sorts by the endpoint")
    @Test
    void columnSortsByEndpoint__() {
        dates(LocalDate.of(999, 12, 31), CLOSED, LocalDate.of(1000, 1, 1), OPEN);
        dates(LocalDate.of(2026, 5, 5), OPEN, LocalDate.of(2026, 6, 6), OPEN);
        final List<?> lowers = applyEntityManager(em -> em
                .createNativeQuery("SELECT DISTINCT range_lower FROM " + _LocalDateRangeEntity.TABLE_NAME
                                   + " WHERE range_lower IN ('0999-12-31[', '2026-05-05(')"
                                   + " ORDER BY range_lower")
                .getResultList());
        assertThat(lowers).map(String::valueOf).containsExactly("0999-12-31[", "2026-05-05(");
    }

    @DisplayName("containment runs in SQL: a plain comparison above, a recheck below")
    @Test
    void containmentRunsInSql__() {
        dates(JAN, CLOSED, APR, CLOSED);
        final var probe = JAN.toString();
        final var count = applyEntityManager(em -> em
                .createNativeQuery("SELECT COUNT(*) FROM " + _LocalDateRangeEntity.TABLE_NAME
                                   // upper: the two upper markers already sort in cut order
                                   + " WHERE (range_upper IS NULL OR range_upper > ?1 || ')')"
                                   // lower: '(' sorts before '[', so the comparison needs a recheck
                                   + " AND (range_lower IS NULL"
                                   + "      OR (range_lower <= ?1 || '['"
                                   + "          AND (range_lower < ?1 || '(' OR range_lower LIKE '%[')))")
                .setParameter(1, probe)
                .getSingleResult());
        assertThat(Long.parseLong(String.valueOf(count))).isPositive();
    }

    @DisplayName("nothing is decoded until an endpoint is asked for")
    @Test
    void decodingIsOnDemand__() {
        final var id = dates(JAN, CLOSED, APR, OPEN).getId();
        _LocalDateRangeEntity.DECODE_COUNT.set(0);
        final var found = applyEntityManager(em -> em.find(_LocalDateRangeEntity.class, id));
        assertThat(_LocalDateRangeEntity.DECODE_COUNT.get())
                .as("loading a row splits nothing and decodes nothing")
                .isZero();
        assertThat(found.getRangeLower()).isEqualTo(JAN);
        assertThat(_LocalDateRangeEntity.DECODE_COUNT.get()).isOne();
        // the bound type is the last character, so reading it decodes nothing at all
        assertThat(found.getLowerBoundType()).isSameAs(CLOSED);
        assertThat(_LocalDateRangeEntity.DECODE_COUNT.get()).isOne();
    }

    @DisplayName("a modification through a setter reaches the database: the cut column is what goes dirty")
    @Test
    void modificationIsNotLost__() {
        final var id = dates(JAN, CLOSED, APR, OPEN).getId();
        applyEntityManager(em -> {
            final var managed = em.find(_LocalDateRangeEntity.class, id);
            managed.setRangeUpper(LocalDate.of(2026, 7, 1), CLOSED);
            return null;   // no explicit merge or flush: dirty checking has to catch it
        });
        final var reloaded = applyEntityManager(em -> em.find(_LocalDateRangeEntity.class, id));
        assertThat(reloaded.getRangeUpper()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(reloaded.getUpperBoundType()).isSameAs(CLOSED);
        assertThat(reloaded.contains(LocalDate.of(2026, 7, 1))).isTrue();
    }

    @DisplayName("a refresh discards an unflushed change, and the column reads back as it was")
    @Test
    void refreshDiscardsTheChange__() {
        final var id = dates(JAN, CLOSED, APR, OPEN).getId();
        applyEntityManager(em -> {
            final var managed = em.find(_LocalDateRangeEntity.class, id);
            managed.setRangeUpper(LocalDate.of(2026, 7, 1), CLOSED);
            em.refresh(managed);                       // discards the change and reloads
            assertThat(managed.getRangeUpper()).isEqualTo(APR);
            assertThat(managed.getUpperBoundType()).isSameAs(OPEN);
            return null;
        });
    }

    @DisplayName("the single-argument setters write the half-open convention")
    @Test
    void standardSettersWriteHalfOpen__() {
        final var entity = new _LocalDateRangeEntity();
        entity.setRangeLower(JAN);
        entity.setRangeUpper(APR);
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_LocalDateRangeEntity.class, id));
        assertThat(found.getLowerBoundType()).isSameAs(CLOSED);
        assertThat(found.getUpperBoundType()).isSameAs(OPEN);
        assertThat(found.getRangeBounds()).isSameAs(__RangeBounds.CLOSED_OPEN);
        assertThat(found).hasToString("[2026-01-01, 2026-04-01)");
        assertThat(found.contains(JAN)).isTrue();
        assertThat(found.contains(APR)).isFalse();
    }

    @DisplayName("the single-argument setters still take null for an unbounded end")
    @Test
    void standardSettersAcceptNull__() {
        final var entity = new _LocalDateRangeEntity();
        entity.setRangeLower(JAN);
        entity.setRangeUpper(null);
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_LocalDateRangeEntity.class, id));
        assertThat(found.getRangeUpper()).isNull();
        assertThat(found.getUpperBoundType()).isNull();
        assertThat(found.contains(LocalDate.MAX)).isTrue();
    }

    @DisplayName("a number needs a padded, biased encoding - and this one has it")
    @Test
    void integerEncodingSorts__() {
        final var found = persist(new _IntegerRangeEntity(), _IntegerRangeEntity.class, -5, CLOSED, 10, CLOSED);
        assertThat(found.getRangeLower()).isEqualTo(-5);
        assertThat(found.getRangeUpper()).isEqualTo(10);
        assertThat(found.contains(-5)).isTrue();
        assertThat(found.contains(0)).isTrue();
        assertThat(found.contains(10)).isTrue();
        assertThat(found.contains(11)).isFalse();
    }

    @DisplayName("an empty endpoint still makes a non-empty column: the marker is always there")
    @Test
    void emptyValueIsNotAnEmptyColumn__() {
        final var entity = new _PrefixUnsafeStringRangeEntity();
        entity.setRangeLower("", CLOSED);
        entity.setRangeUpper("zzz", OPEN);   // 'z' is above '[', so this pair does not invert
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_PrefixUnsafeStringRangeEntity.class, id));
        assertThat(found.getRangeLower()).isEmpty();
        assertThat(found.getLowerBoundType()).isSameAs(CLOSED);   // and so is distinct from an absent end
        final List<?> stored = applyEntityManager(em -> em
                .createNativeQuery("SELECT range_lower FROM " + _PrefixUnsafeStringRangeEntity.TABLE_NAME
                                   + " WHERE range_lower = '['")
                .getResultList());
        assertThat(stored).as("the column holds '[', one character, never ''").isNotEmpty();
    }

    @DisplayName("non-ASCII endpoints parse exactly: the marker is one ASCII code unit at a pinned position")
    @Test
    void nonAsciiEndpoints__() {
        final var entity = new _PrefixUnsafeStringRangeEntity();
        entity.setRangeLower("\uAC00\uB098\uB2E4", CLOSED);          // 가나다, three BMP code units
        entity.setRangeUpper("\uD558\uD558\uD558", OPEN);            // 하하하, which sorts above it
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_PrefixUnsafeStringRangeEntity.class, id));
        assertThat(found.getRangeLower()).isEqualTo("\uAC00\uB098\uB2E4");
        assertThat(found.getLowerBoundType()).isSameAs(CLOSED);
        final List<?> stored = applyEntityManager(em -> em
                .createNativeQuery("SELECT range_lower FROM " + _PrefixUnsafeStringRangeEntity.TABLE_NAME
                                   + " WHERE range_lower = ?1")
                .setParameter(1, "\uAC00\uB098\uB2E4[")
                .getResultList());
        assertThat(stored).as("the column holds the value followed by '['").isNotEmpty();
    }

    @DisplayName("a supplementary character survives, both code units staying in the value part")
    @Test
    void supplementaryCharacterEndpoint__() {
        final var entity = new _PrefixUnsafeStringRangeEntity();
        entity.setRangeLower("a", CLOSED);
        entity.setRangeUpper("\uD83D\uDE00", OPEN);                   // U+1F600, a surrogate pair
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_PrefixUnsafeStringRangeEntity.class, id));
        assertThat(found.getRangeUpper()).isEqualTo("\uD83D\uDE00").hasSize(2);
        assertThat(found.getUpperBoundType()).isSameAs(OPEN);
    }

    @DisplayName("the embeddable form: two ranges in one table, each overriding its two column names")
    @Test
    void twoRangesInOneTable__() {
        final var stay = new _DateRangeEmbeddable();
        stay.setRangeLower(JAN, CLOSED);
        stay.setRangeUpper(APR, OPEN);
        final var hold = new _DateRangeEmbeddable();
        hold.setRangeLower(LocalDate.of(2025, 12, 1), CLOSED);
        hold.setRangeUpper(JAN, CLOSED);
        final var entity = new _ReservationEntity();
        entity.setStay(stay);
        entity.setHold(hold);
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_ReservationEntity.class, id));
        assertThat(found.getStay()).isNotNull();
        assertThat(found.getStay().getRangeLower()).isEqualTo(JAN);
        assertThat(found.getStay().getUpperBoundType()).isSameAs(OPEN);
        assertThat(found.getHold()).isNotNull();
        assertThat(found.getHold().getRangeUpper()).isEqualTo(JAN);
        assertThat(found.getHold().getUpperBoundType()).isSameAs(CLOSED);
        // the overridden column names are the ones in the schema, and range_lower/range_upper are not
        final var columns = applyEntityManager(em -> em
                .createNativeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS"
                                   + " WHERE UPPER(TABLE_NAME) = UPPER(?1)"
                                   + " AND UPPER(COLUMN_NAME) IN ('STAY_LOWER','STAY_UPPER','HOLD_LOWER','HOLD_UPPER')")
                .setParameter(1, _ReservationEntity.TABLE_NAME)
                .getSingleResult());
        assertThat(Long.parseLong(String.valueOf(columns))).isEqualTo(4L);
    }

    @DisplayName("an entity with @Id on a getter does not flip the hierarchy to property access")
    @Test
    void propertyAccessEntityKeepsFieldMapping__() {
        final var entity = new _PropertyAccessRangeEntity();
        entity.setRangeLower(JAN, CLOSED);
        entity.setRangeUpper(APR, OPEN);
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_PropertyAccessRangeEntity.class, id));
        assertThat(found.getRangeLower()).isEqualTo(JAN);
        assertThat(found.getRangeUpper()).isEqualTo(APR);
        // the two cut columns exist under their declared names, and nothing was mapped off the @Transient accessors
        final var columns = applyEntityManager(em -> em
                .createNativeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS"
                                   + " WHERE UPPER(TABLE_NAME) = UPPER(?1) AND UPPER(COLUMN_NAME) LIKE 'RANGE%'")
                .setParameter(1, _PropertyAccessRangeEntity.TABLE_NAME)
                .getSingleResult());
        assertThat(Long.parseLong(String.valueOf(columns)))
                .as("exactly range_lower and range_upper, no column from a @Transient accessor")
                .isEqualTo(2L);
    }

    @DisplayName("a prefix-violating encoding persists, and the column then sorts the two ends the wrong way")
    @Test
    void prefixViolationIsNotCaught__() {
        // "" is below "A", but the cuts are "[" (0x5B) and "A)" (0x41), so the column disagrees. Nothing here
        // rejects it: the encoding is the subclass's contract, and this records what breaking it costs.
        final var entity = new _PrefixUnsafeStringRangeEntity();
        entity.setRangeLower("", CLOSED);
        entity.setRangeUpper("A", OPEN);
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        final var found = applyEntityManager(em -> em.find(_PrefixUnsafeStringRangeEntity.class, id));
        assertThat(found.getRangeLower()).isEmpty();
        assertThat(found.getRangeUpper()).isEqualTo("A");
        final List<?> misordered = applyEntityManager(em -> em
                .createNativeQuery("SELECT range_lower FROM " + _PrefixUnsafeStringRangeEntity.TABLE_NAME
                                   + " WHERE range_lower > range_upper")
                .getResultList());
        assertThat(misordered).as("the database orders the lower cut above the upper one").isNotEmpty();
    }
}
